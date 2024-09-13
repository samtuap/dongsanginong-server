package org.samtuap.inong.domain.coupon.repository;

import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
