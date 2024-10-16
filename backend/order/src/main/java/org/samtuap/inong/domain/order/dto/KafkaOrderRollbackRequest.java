package org.samtuap.inong.domain.order.dto;

public record KafkaOrderRollbackRequest(Long productId, Long memberId, Long couponId, Long orderId) {
}