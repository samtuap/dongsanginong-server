package org.samtuap.inong.coupon;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.samtuap.inong.domain.coupon.dto.CouponRequestMessage;
import org.samtuap.inong.domain.coupon.consumer.CouponConsumer;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.repository.CouponRedisRepository;
import org.samtuap.inong.domain.coupon.repository.CouponRepository;
import org.samtuap.inong.domain.coupon.repository.MemberCouponRelationRepository;
import org.samtuap.inong.domain.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@SpringBootTest
public class CouponConsumerTest {

    @MockBean
    private CouponService couponService; // CouponService를 MockBean으로 설정

    @Autowired
    private CouponConsumer couponConsumer;

    @Autowired
    private MemberCouponRelationRepository memberCouponRelationRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponRedisRepository couponRedisRepository;

    private Long couponId;
    private Long memberId = 1L;

    @BeforeEach
    public void setup() {
        // 초기 데이터 설정
        memberCouponRelationRepository.deleteAll();
        couponRepository.deleteAll();
        couponRedisRepository.deleteAll();

        Coupon coupon = Coupon.builder()
                .couponName("test-coupon")
                .discountPercentage(10)
                .expiration(LocalDateTime.now().plusDays(1))
                .quantity(10)
                .farmId(1L)
                .build();
        couponRepository.save(coupon);
        couponId = coupon.getId(); // 저장 후 생성된 id를 가져옴
        couponRedisRepository.createCoupon(couponId, 10);
    }

    @AfterEach
    public void cleanup() {
        // 테스트 데이터 정리
        memberCouponRelationRepository.deleteAll();
        couponRepository.deleteAll();
        couponRedisRepository.deleteAll();
    }

    @Test
    void Consumer_실패시_Data_일관성_유지_검증() {
        // 쿠폰 수량 감소 시뮬레이션
        couponRedisRepository.decreaseCouponQuantity(couponId);

        // CouponService.processCouponIssue가 예외를 던지도록 설정
        doThrow(new RuntimeException("Consumer 처리 실패")).when(couponService).processCouponIssue(any(), any());

        // 메시지 생성
        CouponRequestMessage message = CouponRequestMessage.builder()
                .couponId(couponId)
                .memberId(memberId)
                .build();

        // Consumer에 메시지 전달
        couponConsumer.consume(message);

        // Awaitility를 사용하여 롤백이 완료될 때까지 대기
        Awaitility.await().atMost(5, SECONDS).until(() -> {
            int remainingQuantity = couponRedisRepository.getCouponQuantity(couponId);
            return remainingQuantity == 10;
        });

        // 데이터베이스에 MemberCouponRelation이 생성되지 않았는지 확인
        boolean exists = memberCouponRelationRepository.existsByCouponIdAndMemberId(couponId, memberId);
        assertThat(exists).isFalse();

        // Redis 수량이 롤백되었는지 확인
        int remainingQuantity = couponRedisRepository.getCouponQuantity(couponId);
        assertThat(remainingQuantity).isEqualTo(10);
    }
}