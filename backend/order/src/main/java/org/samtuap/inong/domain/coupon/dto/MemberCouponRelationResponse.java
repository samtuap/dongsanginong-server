package org.samtuap.inong.domain.coupon.dto;

import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;

import java.time.LocalDateTime;

public record MemberCouponRelationResponse(
        Long couponId,
        Long memberId,
        String useYn,
        LocalDateTime issuedAt
) {
    public static MemberCouponRelationResponse fromEntity(MemberCouponRelation relation) {
        return new MemberCouponRelationResponse(
                relation.getCoupon().getId(),
                relation.getMemberId(),
                relation.getUseYn(),
                relation.getIssuedAt()
        );
    }
}
