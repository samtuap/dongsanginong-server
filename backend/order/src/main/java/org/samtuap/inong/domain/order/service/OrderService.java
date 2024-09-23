package org.samtuap.inong.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.repository.CouponRepository;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.order.dto.MemberAllInfoResponse;
import org.samtuap.inong.domain.order.dto.PaymentRequest;
import org.samtuap.inong.domain.order.dto.PaymentResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.samtuap.inong.common.exceptionType.CouponExceptionType.CANNOT_APPLY_COUPON;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberFeign memberFeign;
    private final ProductFeign productFeign;
    private final CouponRepository couponRepository;

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
        // 1. 멤버 정보, 패키지 상품 정보 가져오기
        MemberAllInfoResponse memberInfo = memberFeign.getMemberAllInfoById(memberId);
        PackageProductResponse packageProduct = productFeign.getPackageProduct(reqDto.packageId());
        Coupon coupon = couponRepository.findByIdOrThrow(reqDto.couponId());

        // 2. 최초 결제하기
        Long totalAmount = packageProduct.price();
        Long finalAmount = totalAmount;
        Long discountAmount = 0L;
        if(coupon != null) {
            discountAmount = calculateDiscountAmount(totalAmount, coupon, packageProduct);
            finalAmount = totalAmount - discountAmount;
        }
        firstPayment(memberInfo, packageProduct, finalAmount);

        // TODO: 3. 결제 정보 저장하기
        Ordering order = Ordering.builder()
                .memberId(memberId)
                .packageId(reqDto.packageId())
                .farmId(packageProduct.farmId())
                .totalPrice(totalAmount)
                .discountPrice(discountAmount)
                .build();
        Ordering savedOrder = orderRepository.save(order);

        // TODO: 4. 배송 정보 저장하기

        // TODO: 5. 다음 결제 예약하기

        return PaymentResponse.builder()
                .orderId(savedOrder.getId())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }

    private String firstPayment(MemberAllInfoResponse memberInfo,
                              PackageProductResponse packageInfo,
                              Long paidAmount) {
        // 포트원 빌링키 결제 API URL
        String paymentId = PAYMENT_PREFIX + String.valueOf(UUID.randomUUID());
        String url = "https://api.portone.io/payments/" + paymentId + "/billing-key";

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
            // 응답 처리
            log.info("[Debug. OrderService. line 101] {}", response.getBody());
            return response.getBody();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return "failed";
    }

    private Long calculateDiscountAmount(Long originalPrice, Coupon coupon, PackageProductResponse packageInfo) {
        // 구매하려는 상품에 적용할 수 있는 쿠폰인지 검증
        // 검증: 1. 적용할 수 있는 쿠폰인가? 2. 멤버가 이미 발급 받았는가? 3. 이미 사용하지는 않았는가?
        if(!coupon.getFarmId().equals(packageInfo.farmId())) {
            throw new BaseCustomException(CANNOT_APPLY_COUPON);
        }

        return (long)((double)originalPrice * ((double)coupon.getDiscountPercentage() / 100.0));

    }
}
