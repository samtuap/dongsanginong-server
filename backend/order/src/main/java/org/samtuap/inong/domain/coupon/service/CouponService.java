package org.samtuap.inong.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.coupon.dto.*;
import org.samtuap.inong.domain.coupon.entity.Coupon;
import org.samtuap.inong.domain.coupon.entity.MemberCouponRelation;
import org.samtuap.inong.domain.coupon.repository.CouponRedisRepository;
import org.samtuap.inong.domain.coupon.repository.CouponRepository;
import org.samtuap.inong.domain.coupon.repository.MemberCouponRelationRepository;
import org.samtuap.inong.domain.delivery.dto.FarmDetailGetResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.samtuap.inong.common.exceptionType.CouponExceptionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final MemberCouponRelationRepository memberCouponRelationRepository;
    private final ProductFeign productFeign;
    private final CouponRedisRepository couponRedisRepository;

    @Transactional
    public Long createCoupon(Long sellerId, CouponCreateRequest request) {

        FarmDetailGetResponse farm = productFeign.getFarmInfoWithSeller(sellerId);

        // Coupon 엔티티 생성
        Coupon coupon = request.toEntity(request, farm.id());
        couponRepository.save(coupon);

        couponRedisRepository.setCouponQuantity(coupon.getId(), coupon.getQuantity());

        return coupon.getId();
    }

    @Transactional
    public List<Coupon> getCouponsBySellerId(Long sellerId) {

        FarmDetailGetResponse farm = productFeign.getFarmInfoWithSeller(sellerId);
        Long farmId = farm.id();  // farmId 추출

        // farmId로 쿠폰 목록을 조회
        return couponRepository.findAllByFarmId(farmId);
    }

    @Transactional
    public void processCouponIssue(Long couponId, Long memberId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BaseCustomException(COUPON_NOT_FOUND));

        // 쿠폰 유효기간 검증
        if (coupon.getExpiration().isBefore(LocalDateTime.now())) {
            throw new BaseCustomException(COUPON_EXPIRED);
        }
        // 중복 다운로드 검증
        boolean isAlreadyDownloaded = memberCouponRelationRepository.existsByCouponIdAndMemberId(couponId, memberId);
        if (isAlreadyDownloaded) {
            throw new BaseCustomException(ALREADY_DOWNLOADED_COUPON);
        }

        MemberCouponRelationRequest request = new MemberCouponRelationRequest(memberId, "N" );
        MemberCouponRelation memberCouponRelation = request.toEntity(coupon);
        memberCouponRelationRepository.save(memberCouponRelation);

        if (coupon.getQuantity() != -1) { // 무제한 쿠폰은 수량 감소하지 않음
            coupon.decreaseQuantity();
            couponRepository.save(coupon);
        }

        log.info("쿠폰 ID: {} - 남은 수량: {}", couponId, coupon.getQuantity());
    }

    @Transactional(readOnly = true)
    public List<MemberCouponListResponse> getDownloadedCouponsByMember(Long memberId) {
        List<MemberCouponRelation> memberCouponRelations = memberCouponRelationRepository.findAllByMemberIdAndUsedAtIsNull(memberId);

        // MemberCouponRelation 엔티티를 MemberCouponRelationResponse로 변환
        return memberCouponRelations.stream()
                .map(MemberCouponListResponse::fromEntity)
                .toList();
    }


    public AvailableCouponListGetResponse getAvailableCouponList(Long memberId, Long farmId) {
        List<MemberCouponRelation> memberCouponRelations = memberCouponRelationRepository.findAllByMemberIdAndFarmIdAndUsedAtIsNull(memberId, farmId);
        log.info("line 84 >>>> {}", memberCouponRelations);
        List<AvailableCouponGetResponse> list = memberCouponRelations.stream().map(AvailableCouponGetResponse::fromEntity).toList();
        return new AvailableCouponListGetResponse(list);
    }
}
