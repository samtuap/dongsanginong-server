package org.samtuap.inong.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.coupon.dto.*;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;
import org.samtuap.inong.domain.coupon.repository.CouponRepository;
import org.samtuap.inong.domain.coupon.repository.MemberCouponRelationRepository;
import org.samtuap.inong.domain.delivery.dto.FarmDetailGetResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.samtuap.inong.common.exceptionType.CouponExceptionType.*;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final MemberCouponRelationRepository memberCouponRelationRepository;
    private final ProductFeign productFeign;

    @Transactional
    public void createCoupon(Long sellerId, CouponCreateRequest request) {

        FarmDetailGetResponse farm = productFeign.getFarmInfoWithSeller(sellerId);

        // Coupon 엔티티 생성
        Coupon coupon = request.toEntity(request, farm.id());
        couponRepository.save(coupon);
    }

    @Transactional
    public List<Coupon> getCouponsBySellerId(Long sellerId) {

        FarmDetailGetResponse farm = productFeign.getFarmInfoWithSeller(sellerId);
        Long farmId = farm.id();  // farmId 추출

        // farmId로 쿠폰 목록을 조회
        return couponRepository.findAllByFarmId(farmId);
    }


    @Transactional
    public MemberCouponRelationResponse downloadCoupon(Long couponId, String memberId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BaseCustomException(COUPON_NOT_FOUND));

        // 쿠폰 유효기간 검증
        if (coupon.getExpiration().isBefore(LocalDateTime.now())) {
            throw new BaseCustomException(COUPON_EXPIRED);
        }
        // 중복 다운로드 검증
        boolean isAlreadyDownloaded = memberCouponRelationRepository.existsByCouponIdAndMemberId(couponId, Long.parseLong(memberId));
        if (isAlreadyDownloaded) {
            throw new BaseCustomException(ALREADY_DOWNLOADED_COUPON);
        }

        MemberCouponRelationRequest request = new MemberCouponRelationRequest(Long.parseLong(memberId), "N" );
        MemberCouponRelation memberCouponRelation = request.toEntity(coupon);
        memberCouponRelationRepository.save(memberCouponRelation);

        return MemberCouponRelationResponse.fromEntity(memberCouponRelation);
    }


    @Transactional(readOnly = true)
    public List<MemberCouponListResponse> getDownloadedCouponsByMember(String memberId) {
        List<MemberCouponRelation> memberCouponRelations = memberCouponRelationRepository.findAllByMemberIdAndUseYn(Long.parseLong(memberId), "N");

        // MemberCouponRelation 엔티티를 MemberCouponRelationResponse로 변환
        return memberCouponRelations.stream()
                .map(MemberCouponListResponse::fromEntity)
                .toList();
    }


}
