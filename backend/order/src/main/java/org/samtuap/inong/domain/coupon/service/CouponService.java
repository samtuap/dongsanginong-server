package org.samtuap.inong.domain.coupon.service;

import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.domain.coupon.dto.CouponCreateRequest;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public void createCoupon(Long farmId, CouponCreateRequest request) {

        // Coupon 엔티티 생성
        Coupon coupon = request.toEntity(farmId);
        couponRepository.save(coupon);
    }
}
