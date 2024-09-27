package org.samtuap.inong.domain.coupon.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.coupon.dto.CouponCreateRequest;
import org.samtuap.inong.domain.coupon.dto.MemberCouponRelationResponse;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/create")
    public ResponseEntity<?> createCoupon(
            @RequestHeader("sellerId") Long sellerId,
            @RequestBody CouponCreateRequest request) {

        couponService.createCoupon(sellerId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/{farm_id}/list")
    public ResponseEntity<List<Coupon>> getCouponsByFarmId(
            @PathVariable("farm_id") Long farmId) {

        List<Coupon> coupons = couponService.getCouponsByFarmId(farmId);
        return ResponseEntity.ok(coupons);
    }


    @PostMapping("/{id}/download")
    public ResponseEntity<MemberCouponRelationResponse> downloadCoupon(@PathVariable("id") Long couponId,
                                                                       @RequestHeader("myId") String memberId) {
        MemberCouponRelationResponse response = couponService.downloadCoupon(couponId, memberId);
        return new ResponseEntity<>(response ,HttpStatus.OK);
    }


    @GetMapping("/downloaded-coupons")
    public ResponseEntity<List<MemberCouponRelationResponse>> getDownloadedCouponsByMember(
            @RequestHeader("myId") String memberId) {

        List<MemberCouponRelationResponse> response = couponService.getDownloadedCouponsByMember(memberId);
        return ResponseEntity.ok(response);
    }


}
