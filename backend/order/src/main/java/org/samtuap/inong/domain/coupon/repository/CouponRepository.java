package org.samtuap.inong.domain.coupon.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.CouponExceptionType;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    default Coupon findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BaseCustomException(CouponExceptionType.COUPON_NOT_FOUND));
    }
  
    List<Coupon> findAllByFarmId(Long farmId);

}
