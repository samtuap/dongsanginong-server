package org.samtuap.inong.domain.coupon.dto;

import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;

import java.time.LocalDateTime;

public record MemberCouponRelationRequest(
        Long memberId,
        String useYn
) {
    // 엔티티로 변환하는 메서드
    public MemberCouponRelation toEntity(Coupon coupon) {
        return MemberCouponRelation.builder()
                .coupon(coupon)
                .memberId(memberId)
                .useYn(useYn)
                .issuedAt(LocalDateTime.now()) // 발급일 설정
                .build();
    }
}
