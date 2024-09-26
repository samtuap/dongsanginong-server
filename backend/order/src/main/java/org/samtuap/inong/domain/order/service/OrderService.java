package org.samtuap.inong.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;
import org.samtuap.inong.domain.coupon.repository.CouponRepository;
import org.samtuap.inong.domain.coupon.repository.MemberCouponRelationRepository;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.delivery.repository.DeliveryRepository;
import org.samtuap.inong.domain.order.dto.*;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.samtuap.inong.domain.receipt.entity.PaymentMethod;
import org.samtuap.inong.domain.receipt.entity.PaymentStatus;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.samtuap.inong.domain.receipt.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.samtuap.inong.common.exceptionType.CouponExceptionType.*;
import static org.samtuap.inong.common.exceptionType.OrderExceptionType.*;
import static org.samtuap.inong.domain.delivery.entity.DeliveryStatus.*;
import static org.samtuap.inong.domain.order.entity.CancelReason.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberFeign memberFeign;
    private final ProductFeign productFeign;
    private final CouponRepository couponRepository;
    private final DeliveryRepository deliveryRepository;
    private final MemberCouponRelationRepository memberCouponRelationRepository;
    private final ReceiptRepository receiptRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${portone.api-secret}")
    private String API_SECRET;

    @Value("${portone.payment-prefix}")
    private String PAYMENT_PREFIX;

    @Value("${portone.store-id}")
    private String STORE_ID;

    @Value("${portone.channel-key}")
    private String CHANNEL_KEY;

    public List<Long> getTopPackages() {
        return orderRepository.findTop10PackageIdWithMostOrders();
    }

    @Transactional
    public PaymentResponse makeFirstOrder(Long memberId, PaymentRequest reqDto) {
        PaymentResponse paymentResponse = makeOrder(memberId, reqDto);
        KafkaSubscribeProductRequest request = new KafkaSubscribeProductRequest(reqDto.packageId(), memberId, reqDto.couponId());
        kafkaTemplate.send("subscription-topic", request);
        return paymentResponse;
    }

    @Transactional
    public PaymentResponse makeOrder(Long memberId, PaymentRequest reqDto) {
        // 1. 멤버 정보, 패키지 상품 정보 가져오기
        MemberAllInfoResponse memberInfo = memberFeign.getMemberAllInfoById(memberId);
        PackageProductResponse packageProduct = productFeign.getPackageProduct(reqDto.packageId());

        Coupon coupon = null;
        if(reqDto.couponId() != null) {
            coupon = couponRepository.findByIdOrThrow(reqDto.couponId());
        }

        // 2. 최초 결제 정보 저장하기
        Long originalAmount = packageProduct.price();
        Long paidAmount = originalAmount;
        Long discountAmount = 0L;
        if(coupon != null) {
            discountAmount = calculateDiscountAmount(coupon, originalAmount);
            paidAmount = originalAmount - discountAmount;
        }


        Ordering order = Ordering.builder()
                .memberId(memberId)
                .packageId(reqDto.packageId())
                .farmId(packageProduct.farmId())
                .build();
        Ordering savedOrder = orderRepository.save(order);

        if(coupon != null) {
            useCoupon(coupon, order, packageProduct, memberId);
        }


        // 3. 배송 정보 저장하기
        switch (packageProduct.delivery_cycle()) {
            case 1, 4, 7, 14, 28 -> saveDeliveries(order, packageProduct);
            default -> throw new BaseCustomException(INVALID_PACKAGE_PRODUCT);
        }

        // 5. 최초 결제하기
        String paymentId = kakaoPay(memberInfo, packageProduct, paidAmount, order);

        // 4. 영수증 만들기
        makeReceipt(savedOrder, packageProduct, paidAmount, paymentId);



        return PaymentResponse.builder()
                .orderId(savedOrder.getId())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }


    protected String kakaoPay(MemberAllInfoResponse memberInfo,
                                PackageProductResponse packageInfo,
                                Long paidAmount,
                                Ordering order) {
        // 포트원 빌링키 결제 API URL
        String paymentId = PAYMENT_PREFIX + "-" + UUID.randomUUID();
        String url = "https://api.portone.io/payments/" + paymentId + "/billing-key";
        order.updatePaymentId(paymentId);

        if(memberInfo.billingKey() == null || memberInfo.billingKey().isEmpty()) {
            throw new BaseCustomException(BILLING_KEY_NOT_FOUND);
        }


        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "PortOne " + API_SECRET);

        // 요청 바디 설정
        Map<String, Object> body = new HashMap<>();
        body.put("billingKey", memberInfo.billingKey());
        body.put("orderName", packageInfo.packageName());
        body.put("storeId", STORE_ID);
        body.put("channelKey", CHANNEL_KEY);


        // 고객 정보 (필요 시 추가)
        Map<String, Object> customer = new HashMap<>();
        customer.put("customerId", memberInfo.id());
        customer.put("customerEmail", memberInfo.email());
        customer.put("customerName", memberInfo.name());
        body.put("customer", customer);

        // 결제 금액 및 통화 설정
        Map<String, Object> amount = new HashMap<>();
        amount.put("total", paidAmount);
        body.put("amount", amount);
        body.put("currency", "KRW");

        // HttpEntity에 요청 데이터 추가
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // RestTemplate을 이용해 POST 요청 전송
        ResponseEntity<String> response = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BaseCustomException(FAIL_TO_PAY);
        }

        return paymentId;
    }

    protected Long calculateDiscountAmount(Coupon coupon, Long originalPrice) {
        return (long)((double)originalPrice * ((double)coupon.getDiscountPercentage() / 100.0));
    }

    protected void saveDeliveries(Ordering ordering, PackageProductResponse packageProduct) {
        int times = 28 / packageProduct.delivery_cycle();
        for(int time = 0; time < times; time++) {
            Delivery delivery = Delivery.builder()
                    .ordering(ordering)
                    .deliveryStatus(BEFORE_DELIVERY)
                    .deliveryDueDate(LocalDate.now().plusDays(time))
                    .courier("CJ") // FIXME: 논의 필요
                    .build();
            deliveryRepository.save(delivery);
        }
    }


    protected void useCoupon(Coupon coupon, Ordering order, PackageProductResponse packageInfo, Long memberId) {
        // 구매하려는 상품에 적용할 수 있는 쿠폰인지 검증
        // [검증 1] 적용할 수 있는 쿠폰인가?
        if(!coupon.getFarmId().equals(packageInfo.farmId())) {
            throw new BaseCustomException(CANNOT_APPLY_COUPON);
        }

        // [검증 2] 발급 받았는가
        Optional<MemberCouponRelation> memberCouponOpt = memberCouponRelationRepository.findByCouponIdAndMemberId(coupon.getId(), memberId);
        if(memberCouponOpt.isEmpty()) {
            throw new BaseCustomException(COUPON_NOT_ISSUED);
        }

        // [검증 3] 시용되지 않았는가
        MemberCouponRelation memberCoupon = memberCouponOpt.get();
        if(memberCoupon.getUseYn().equals("Y")) {
            throw new BaseCustomException(COUPON_ALREADY_USED);
        }

        // 쿠폰 사용
        memberCoupon.updateIsUsed("Y");
        memberCoupon.updateUsedAt(LocalDateTime.now());
        memberCoupon.updateOrderId(order.getId());
    }

    public void regularPay() {
        SubscriptionListGetResponse response = memberFeign.getSubscriptionToPay();

        List<SubscriptionListGetResponse.SubscriptionGetResponse> subscriptions = response.subscriptions();
        for (SubscriptionListGetResponse.SubscriptionGetResponse subscription : subscriptions) {
            makeOrder(subscription.getMemberId(), new PaymentRequest(subscription.getPackageId(), null));
        }
    }

    public void makeReceipt(Ordering order, PackageProductResponse packageProduct, Long paidAmount, String paymentId) {
        Receipt receipt = Receipt.builder().order(order)
                .paidAt(order.getCreatedAt())
                .beforePrice(packageProduct.price())
                .discountPrice(packageProduct.price() - paidAmount)
                .totalPrice(packageProduct.price())
                .paymentStatus(PaymentStatus.PAID)
                .paymentMethod(PaymentMethod.KAKAOPAY) // TODO: 추후 확장 가능성 있음
                .portOnePaymentId(paymentId)
                .build();

        receiptRepository.save(receipt);
    }

    public List<OrderDeliveryListResponse> getOrderDeliveryList(Long memberId) {
        return orderRepository.findByMemberId(memberId).stream()
                .map(ordering -> {
                    PackageProductResponse product = productFeign.getPackageProduct(ordering.getPackageId());
                    Delivery delivery = deliveryRepository.findByOrdering(ordering);
                    return delivery != null && delivery.getDeliveryAt() != null
                            ? OrderDeliveryListResponse.fromEntity(ordering, product, delivery)
                            : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<OrderPaymentListResponse> getOrderPaymentList(Long memberId) {
        return orderRepository.findAllByMemberId(memberId).stream()
                .map(ordering -> {
                    PackageProductResponse product = productFeign.getPackageProduct(ordering.getPackageId());
                    Receipt receipt = receiptRepository.findByOrderOrThrow(ordering);
                    return OrderPaymentListResponse.from(ordering, product, receipt);
                })
                .collect(Collectors.toList());
    }

    //== Kafka로 주문/결제 취소 ==//
    @Transactional
    @KafkaListener(topics = "order-rollback-topic", groupId = "member-group",/*member group으로 부터 메시지가 들어오면*/ containerFactory = "kafkaListenerContainerFactory")
    public void consumeRollbackEvent(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        KafkaOrderRollbackRequest rollbackRequest = null;
        try {
            rollbackRequest = objectMapper.readValue(message, KafkaOrderRollbackRequest.class);
            this.rollbackOrder(rollbackRequest);
        } catch (JsonProcessingException e) {
            log.error("[rollback error] line 283: 카프카 메시지 파싱 에러");
            e.printStackTrace();
            throw new BaseCustomException(INVALID_ROLLBACK_REQUEST);
        } catch(Exception e) {
            log.error("[rollback error] line 284: 결제 실패");
            e.printStackTrace();
        }
    }

    protected void rollbackOrder(KafkaOrderRollbackRequest rollbackRequest) throws InterruptedException {
        Optional<Ordering> orderOpt = orderRepository
                .findByPackageIdAndMemberId(rollbackRequest.productId(), rollbackRequest.memberId());

        Ordering order = orderOpt.get();
        Receipt receipt = receiptRepository.findByOrderOrThrow(order);

        order.updateCanceledAt(LocalDateTime.now());
        order.updateCancelReason(SYSTEM_ERROR);
        receipt.updatePaymentStatus(PaymentStatus.REFUND_PROCESSING);

        // [쿠폰 롤백] 쿠폰을 사용하지 않은 상태로 되돌리기
        if(rollbackRequest.couponId() != null) {
            MemberCouponRelation memberCoupon = memberCouponRelationRepository.findByCouponIdAndMemberId(rollbackRequest.couponId(), rollbackRequest.memberId())
                    .orElseThrow(() -> new BaseCustomException(COUPON_NOT_FOUND));
            memberCoupon.updateIsUsed("N");
            memberCoupon.updateUsedAt(null);
            memberCoupon.updateOrderId(null);
        }

        // 포트원 환불
        kakaoPayRefund(receipt);
    }

    private void kakaoPayRefund(Receipt receipt) {
        String paymentId = receipt.getPortOnePaymentId();
        String url = "https://api.portone.io/payments/" + paymentId + "/cancel";

        log.info("line 326: {}", paymentId);


        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "PortOne " + API_SECRET);

        // 요청 바디 설정
        Map<String, Object> body = new HashMap<>();
        body.put("reason", SYSTEM_ERROR.getDescription()); // 취소 사유
        body.put("storeId", STORE_ID); // 스토어 아이디

        // HttpEntity에 요청 데이터 추가
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BaseCustomException(FAIL_TO_PAY);
        }

        receipt.updatePaymentStatus(PaymentStatus.REFUNDED);
        receipt.updateRefundedAt(LocalDateTime.now());
        receiptRepository.save(receipt);
    }

}
