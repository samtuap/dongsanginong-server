package org.samtuap.inong.domain.chat.dto;

import lombok.Builder;

@Builder
public record CouponMessage(
        String type,
        Long couponId,
        String couponName
) {
}
