package org.samtuap.inong.domain.coupon.dto;

import lombok.Builder;
import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;

import java.time.LocalDateTime;

@Builder
public record AvailableCouponGetResponse(Long couponId,
                                         Long issuedCouponId,
                                         String couponName,
                                         Integer discountRate,
                                         LocalDateTime expiration,
                                         Long farmId) {
    public static AvailableCouponGetResponse fromEntity(MemberCouponRelation mcr) {
        return AvailableCouponGetResponse.builder()
                .couponId(mcr.getCoupon().getId())
                .couponName(mcr.getCoupon().getCouponName())
                .issuedCouponId(mcr.getId())
                .discountRate(mcr.getCoupon().getDiscountPercentage())
                .expiration(mcr.getCoupon().getExpiration())
                .farmId(mcr.getCoupon().getFarmId())
                .build();
    }
}
