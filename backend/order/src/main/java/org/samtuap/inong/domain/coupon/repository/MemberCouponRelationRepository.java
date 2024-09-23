package org.samtuap.inong.domain.coupon.repository;

import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCouponRelationRepository extends JpaRepository<MemberCouponRelation, Long> {
    boolean existsByCouponIdAndMemberId(Long couponId, long memberId);
}
