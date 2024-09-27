package org.samtuap.inong.domain.coupon.dto;

import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.coupon.entity.Coupon;

import java.time.LocalDateTime;

public record CouponCreateRequest(
        @NotNull String couponName,
        @NotNull Integer discountPercentage,
        @NotNull LocalDateTime expiration,
        @NotNull Long farmId
) {
    public static Coupon toEntity(CouponCreateRequest dto, Long farmId) {
        return Coupon.builder()
                .couponName(dto.couponName)
                .discountPercentage(dto.discountPercentage)
                .expiration(dto.expiration)
                .farmId(farmId)
                .build();
    }
}
