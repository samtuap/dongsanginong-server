package org.samtuap.inong.domain.subscription.dto;

import lombok.Builder;

@Builder
public record SubscribeProductRequest(Long productId, Long memberId, Long couponId, Long orderId) {
}
