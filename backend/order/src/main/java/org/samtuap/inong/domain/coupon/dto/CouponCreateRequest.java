package org.samtuap.inong.domain.coupon.dto;

import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.coupon.entity.Coupon;

import java.time.LocalDate;
import java.time.LocalTime;

public record CouponCreateRequest(
        @NotNull String couponName,
        @NotNull Integer discountPercentage,
        @NotNull LocalDate expirationDate,  // LocalDate로 날짜 받기
        @NotNull LocalTime expirationTime,  // LocalTime으로 시간 받기
        @NotNull Long farmId
) {
    public static Coupon toEntity(CouponCreateRequest dto, Long farmId) {
        // LocalDate와 LocalTime을 LocalDateTime으로 합치기
        return Coupon.builder()
                .couponName(dto.couponName)
                .discountPercentage(dto.discountPercentage)
                .expiration(dto.expirationDate.atTime(dto.expirationTime))  // LocalDateTime으로 변환
                .farmId(farmId)
                .build();
    }
}
