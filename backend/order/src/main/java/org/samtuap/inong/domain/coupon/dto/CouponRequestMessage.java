package org.samtuap.inong.domain.coupon.dto;

import lombok.Builder;

@Builder
public record CouponRequestMessage(
        Long couponId,
        Long memberId
) {
}
