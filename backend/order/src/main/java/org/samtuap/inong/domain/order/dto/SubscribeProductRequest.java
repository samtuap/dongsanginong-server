package org.samtuap.inong.domain.order.dto;

import lombok.Builder;

@Builder
public record SubscribeProductRequest(Long productId, Long memberId, Long couponId, Long orderId) {
}
