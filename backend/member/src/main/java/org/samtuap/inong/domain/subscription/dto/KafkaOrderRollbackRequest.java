package org.samtuap.inong.domain.subscription.dto;

public record KafkaOrderRollbackRequest(Long productId, Long memberId, Long couponId) {
}
