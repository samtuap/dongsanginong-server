package org.samtuap.inong.coupon;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.samtuap.inong.domain.coupon.dto.CouponCreateRequest;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.producer.CouponProducer;
import org.samtuap.inong.domain.coupon.repository.CouponRedisRepository;
import org.samtuap.inong.domain.coupon.repository.CouponRepository;
import org.samtuap.inong.domain.coupon.repository.MemberCouponRelationRepository;
import org.samtuap.inong.domain.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest
public class CouponServiceTest {

    @Autowired
    private CouponRedisRepository couponRedisRepository;
    @Autowired
    private CouponService couponService;
    @Autowired
    private MemberCouponRelationRepository memberCouponRelationRepository;
    @Autowired
    private CouponProducer couponProducer;
    @Autowired
    private CouponRepository couponRepository;

    private Long couponId;
    private final Integer maxCount = 100;

    @BeforeEach
    public void setup() {
        // 테스트 시작 전 데이터 초기화
        memberCouponRelationRepository.deleteAll();
        couponRepository.deleteAll();
        couponRedisRepository.deleteAll();

        // 쿠폰 생성 및 Redis에 수량 설정
        CouponCreateRequest request = new CouponCreateRequest(
                "test-coupon", 10, LocalDate.now().plusDays(1),
                LocalTime.of(23, 59), 1L, maxCount
        );
        couponId = couponService.createCoupon(19L, request);
        couponRedisRepository.createCoupon(couponId, maxCount);
    }

    @AfterEach
    public void cleanup() {
        // 테스트 종료 후 데이터 정리
        memberCouponRelationRepository.deleteAll();
        couponRepository.deleteAll();
        couponRedisRepository.deleteAll();
    }

    @Test
    void 쿠폰_동시_발급_테스트() throws InterruptedException {
        int peopleCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(peopleCount);

        for (int i = 0; i < peopleCount; i++) {
            long memberId = i;
            executorService.execute(() -> {
                try {
                    couponProducer.requestCoupon(couponId, memberId);
                } catch (Exception e) {
                    // 예외 처리
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Awaitility를 사용하여 Kafka Consumer가 메시지를 처리할 때까지 대기
        Awaitility.await().atMost(10, SECONDS).until(() -> {
            int issuedCount = memberCouponRelationRepository.countByCouponId(couponId);
            return issuedCount == maxCount;
        });

        // 수량이 0이어야 함
        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getQuantity()).isEqualTo(0);

        // 발급된 쿠폰 수가 maxCount와 동일해야 함
        int issuedCount = memberCouponRelationRepository.countByCouponId(couponId);
        assertThat(issuedCount).isEqualTo(maxCount);
    }

    @Test
    void 무제한_쿠폰_발급_테스트() throws InterruptedException {
        // 무제한 쿠폰 생성
        CouponCreateRequest request = new CouponCreateRequest(
                "unlimited-coupon", 10, LocalDate.now().plusDays(1),
                LocalTime.of(23, 59), 1L, -1
        );
        Long unlimitedCouponId = couponService.createCoupon(19L, request);
        couponRedisRepository.createCoupon(unlimitedCouponId, -1);

        int peopleCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(peopleCount);

        for (int i = 0; i < peopleCount; i++) {
            long memberId = i;
            executorService.execute(() -> {
                try {
                    couponProducer.requestCoupon(unlimitedCouponId, memberId);
                } catch (Exception e) {
                    // 예외 처리
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Awaitility를 사용하여 Kafka Consumer가 메시지를 처리할 때까지 대기
        Awaitility.await().atMost(10, SECONDS).until(() -> {
            long issuedCount = memberCouponRelationRepository.countByCouponId(unlimitedCouponId);
            return issuedCount == peopleCount;
        });

        // 수량이 -1이어야 함 (무제한)
        Coupon coupon = couponRepository.findById(unlimitedCouponId).orElseThrow();
        assertThat(coupon.getQuantity()).isEqualTo(-1);

        // 발급된 쿠폰 수가 peopleCount와 동일해야 함
        long issuedCount = memberCouponRelationRepository.countByCouponId(unlimitedCouponId);
        assertThat(issuedCount).isEqualTo(peopleCount);
    }
}