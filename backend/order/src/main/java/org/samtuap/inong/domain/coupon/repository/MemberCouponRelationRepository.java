package org.samtuap.inong.domain.coupon.repository;

import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberCouponRelationRepository extends JpaRepository<MemberCouponRelation, Long> {
    Optional<MemberCouponRelation> findByCouponIdAndMemberId(Long couponId, Long MemberId);
    boolean existsByCouponIdAndMemberId(Long couponId, long memberId);
    List<MemberCouponRelation> findAllByMemberIdAndUsedAt(Long memberId, LocalDateTime usedAt);


}
