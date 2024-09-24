package org.samtuap.inong.domain.order.service;

import lombok.Builder;
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
import org.samtuap.inong.domain.order.dto.MemberAllInfoResponse;
import org.samtuap.inong.domain.order.dto.PaymentRequest;
import org.samtuap.inong.domain.order.dto.PaymentResponse;
import org.samtuap.inong.domain.order.dto.SubscriptionListGetResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.samtuap.inong.domain.receipt.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.samtuap.inong.common.exceptionType.CouponExceptionType.*;
import static org.samtuap.inong.common.exceptionType.OrderExceptionType.*;
import static org.samtuap.inong.domain.delivery.entity.DeliveryStatus.*;

@Slf4j
@Builder
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
    public PaymentResponse makeOrder(Long memberId, PaymentRequest reqDto) {
        // 1. 멤버 정보, 패키지 상품 정보 가져오기
        MemberAllInfoResponse memberInfo = memberFeign.getMemberAllInfoById(memberId);
        PackageProductResponse packageProduct = productFeign.getPackageProduct(reqDto.packageId());
        Coupon coupon = couponRepository.findByIdOrThrow(reqDto.couponId());

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
                .totalPrice(paidAmount)
                .discountPrice(discountAmount)
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

        // 4. 최초 결제하기
        kakaoPay(memberInfo, packageProduct, paidAmount, order);

        return PaymentResponse.builder()
                .orderId(savedOrder.getId())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }


    protected void kakaoPay(MemberAllInfoResponse memberInfo,
                                PackageProductResponse packageInfo,
                                Long paidAmount,
                                Ordering order) {
        // 포트원 빌링키 결제 API URL
        String paymentId = PAYMENT_PREFIX + "_" + UUID.randomUUID();
        String url = "https://api.portone.io/payments/" + paymentId + "/billing-key";
        order.updatePaymentId(paymentId);

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

        List<PackageProductResponse> packageProducts = response.packageProducts();
        List<SubscriptionListGetResponse.SubscriptionGetResponse> subscriptions = response.subscriptions();
        for (SubscriptionListGetResponse.SubscriptionGetResponse subscription : subscriptions) {
            makeOrder(subscription.getMemberId(), new PaymentRequest(subscription.getPackageId(), null));
        }
    }

//    public void makeReceipt(Ordering order, PackageProductResponse packageProduct, Long paidAmount) {
//        Receipt.builder().order(order)
//                .payedAt()
//                .beforePrice(packageProduct.price())
//                .discountPrice(packageProduct.price() - paidAmount)
//
//    }
}
