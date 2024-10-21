package org.samtuap.inong.coupon;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.samtuap.inong.domain.coupon.dto.CouponRequestMessage;
import org.samtuap.inong.domain.coupon.producer.CouponProducer;
import org.samtuap.inong.domain.coupon.repository.CouponRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CouponProducerTest {

    @MockBean
    private KafkaTemplate<String, CouponRequestMessage> kafkaTemplate;

    @Autowired
    private CouponProducer couponProducer;

    @Autowired
    private CouponRedisRepository couponRedisRepository;

    private Long couponId = 1L;
    private Long memberId = 1L;

    @BeforeEach
    public void setup() {
        // 테스트 시작 전 Redis에 쿠폰 수량 설정
        couponRedisRepository.createCoupon(couponId, 10);
    }

    @AfterEach
    public void cleanup() {
        // 테스트 종료 후 Redis 데이터 정리
        couponRedisRepository.deleteCoupon(couponId);
    }

    @Test
    void kafka_발행_실패시_Redis_수량_롤백_검증() {
        // KafkaTemplate.send가 예외를 던지도록 설정
        CompletableFuture future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka 발행 실패"));
        when(kafkaTemplate.send(anyString(), any(CouponRequestMessage.class))).thenReturn(future);

        // 쿠폰 발급 요청
        couponProducer.requestCoupon(couponId, memberId);

        // Awaitility를 사용하여 롤백이 완료될 때까지 대기
        Awaitility.await().atMost(5, SECONDS).until(() -> {
            int remainingQuantity = couponRedisRepository.getCouponQuantity(couponId);
            return remainingQuantity == 10;
        });

        // Redis 수량이 원래대로 롤백되었는지 확인
        int remainingQuantity = couponRedisRepository.getCouponQuantity(couponId);
        assertThat(remainingQuantity).isEqualTo(10);

        // KafkaTemplate.send가 정확히 한 번 호출되었는지 검증
        verify(kafkaTemplate, times(1)).send(anyString(), any(CouponRequestMessage.class));

//        롤백 돼서 테스트 성공 10개
    }
}