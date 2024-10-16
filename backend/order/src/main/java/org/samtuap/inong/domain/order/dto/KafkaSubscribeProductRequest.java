package org.samtuap.inong.domain.order.dto;

public record KafkaSubscribeProductRequest(Long productId, Long memberId, Long couponId, Long orderId) {
}
