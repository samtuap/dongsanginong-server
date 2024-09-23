package org.samtuap.inong.domain.coupon.dto;

import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.coupon.entity.Coupon;

import java.time.LocalDateTime;

public record CouponCreateRequest(
        @NotNull String couponName,
        @NotNull Integer discountPercentage,
        @NotNull LocalDateTime expiration,
        @NotNull Long farmId // farmId 추가
) {
    public Coupon toEntity(Long farmId) {
        return Coupon.builder()
                .couponName(this.couponName)
                .discountPercentage(this.discountPercentage)
                .expiration(this.expiration)
                .farmId(farmId) // farmId로 Farm 객체 생성
                .build();
    }
}
