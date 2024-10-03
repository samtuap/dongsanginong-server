package org.samtuap.inong.domain.coupon.dto;

import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;

import java.time.LocalDateTime;

public record MemberCouponListResponse(
        Long couponId,
        String couponName,
        Integer discountPercentage,
        LocalDateTime expiration,
        Long memberId,
        String useYn,
        LocalDateTime issuedAt
) {
    public static MemberCouponListResponse fromEntity(MemberCouponRelation relation) {
        return new MemberCouponListResponse(
                relation.getCoupon().getId(),
                relation.getCoupon().getCouponName(),          // 쿠폰명 추가
                relation.getCoupon().getDiscountPercentage(),  // 할인율 추가
                relation.getCoupon().getExpiration(),          // 유효기간 추가
                relation.getMemberId(),
                relation.getUseYn(),
                relation.getIssuedAt()
        );
    }
}
