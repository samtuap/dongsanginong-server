package org.samtuap.inong.domain.coupon.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.coupon.dto.AvailableCouponListGetResponse;
import org.samtuap.inong.domain.coupon.dto.CouponCreateRequest;
import org.samtuap.inong.domain.coupon.dto.MemberCouponListResponse;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.producer.CouponProducer;
import org.samtuap.inong.domain.coupon.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/coupon")
public class CouponController {
    private final CouponService couponService;
    private final CouponProducer couponProducer;

    @PostMapping("/create")
    public ResponseEntity<?> createCoupon(
            @RequestHeader("sellerId") Long sellerId,
            @RequestBody CouponCreateRequest request) {

        couponService.createCoupon(sellerId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/list")
    public ResponseEntity<List<Coupon>> getCouponsBySellerId(
            @RequestHeader("sellerId") Long sellerId) {

        List<Coupon> coupons = couponService.getCouponsBySellerId(sellerId);
        return ResponseEntity.ok(coupons);
    }


    @PostMapping("/{couponId}/download")
    public ResponseEntity<String> downloadCoupon(@PathVariable("couponId") Long couponId, @RequestHeader("myId") Long memberId) {
        try {
            couponProducer.requestCoupon(couponId, memberId);
            return new ResponseEntity<>("쿠폰 발급 신청이 완료되었습니다.", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("쿠폰 다운로드 중 예외 발생: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/downloaded-coupon")
    public ResponseEntity<List<MemberCouponListResponse>> getDownloadedCouponsByMember(
            @RequestHeader("myId") Long memberId) {

        List<MemberCouponListResponse> response = couponService.getDownloadedCouponsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<AvailableCouponListGetResponse> getAvailableCouponList(@RequestHeader("myId") Long memberId,
                                                                                 @PathVariable("farmId") Long farmId) {
        AvailableCouponListGetResponse response = couponService.getAvailableCouponList(memberId, farmId);
        return ResponseEntity.ok(response);
    }
}
