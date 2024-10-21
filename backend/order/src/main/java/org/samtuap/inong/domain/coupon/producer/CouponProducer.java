package org.samtuap.inong.domain.coupon.producer;

import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.coupon.dto.CouponRequestMessage;
import org.samtuap.inong.domain.coupon.repository.CouponRedisRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class CouponProducer {

    private final KafkaTemplate<String, CouponRequestMessage> kafkaTemplate;
    private final CouponRedisRepository couponRedisRepository;

    private static final String TOPIC = "coupon-issue-topic";

    public CouponProducer(@Qualifier("couponKafkaTemplate") KafkaTemplate<String, CouponRequestMessage> kafkaTemplate,
                          CouponRedisRepository couponRedisRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.couponRedisRepository = couponRedisRepository;
    }

    public void requestCoupon(Long couponId, Long memberId) {
        try {
            // Redis에서 쿠폰 수량 감소
            Long remainQuantity = couponRedisRepository.decreaseCouponQuantity(couponId);

            if (remainQuantity == null || remainQuantity < 0) {
                throw new RuntimeException("쿠폰이 소진되었습니다.");
            }

            // Kafka에 메시지 전송
            CouponRequestMessage message = CouponRequestMessage.builder()
                    .couponId(couponId)
                    .memberId(memberId)
                    .build();

            CompletableFuture<SendResult<String, CouponRequestMessage>> future = kafkaTemplate.send(TOPIC, message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Kafka 메시지 전송 성공: {}", message);
                } else {
                    log.error("Kafka 메시지 전송 실패: {}. Redis 수량을 롤백합니다.", message, ex);
                    // Redis 수량 롤백
                    couponRedisRepository.increaseCouponQuantity(couponId);
                }
            });

        } catch (Exception e) {
            log.error("쿠폰 발급 요청 중 예외 발생: {}", e.getMessage());
            throw e;
        }
    }
}