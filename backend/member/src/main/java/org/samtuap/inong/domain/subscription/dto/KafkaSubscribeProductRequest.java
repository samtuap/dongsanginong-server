package org.samtuap.inong.domain.subscription.dto;

public record KafkaSubscribeProductRequest(Long productId, Long memberId, Long couponId) {
}
