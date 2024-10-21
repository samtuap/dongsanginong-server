package org.samtuap.inong.domain.coupon.repository;

import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRelationRepository extends JpaRepository<MemberCouponRelation, Long> {
    Optional<MemberCouponRelation> findByCouponIdAndMemberId(Long couponId, Long MemberId);
    boolean existsByCouponIdAndMemberId(Long couponId, long memberId);
    List<MemberCouponRelation> findAllByMemberIdAndUsedAtIsNull(Long memberId);

    @Query("SELECT mcr FROM MemberCouponRelation mcr" +
            " JOIN FETCH mcr.coupon" +
            " WHERE mcr.memberId = :memberId" +
            " AND mcr.coupon.farmId = :farmId" +
            " AND mcr.usedAt IS NULL")
    List<MemberCouponRelation> findAllByMemberIdAndFarmIdAndUsedAtIsNull(@Param("memberId") Long memberId, @Param("farmId") Long farmId);


    int countByCouponId(Long couponId);
}
