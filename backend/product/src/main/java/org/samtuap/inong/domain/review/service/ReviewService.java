package org.samtuap.inong.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.repository.PackageProductRepository;
import org.samtuap.inong.domain.review.dto.ReviewCreateRequest;
import org.samtuap.inong.domain.review.dto.ReviewResponse;
import org.samtuap.inong.domain.review.entity.Review;
import org.samtuap.inong.domain.review.entity.ReviewImage;
import org.samtuap.inong.domain.review.repository.ReviewImageRepository;
import org.samtuap.inong.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.samtuap.inong.common.exceptionType.ReviewExceptionType.REVIEW_FOUND;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final PackageProductRepository packageProductRepository;

    @Transactional
    public ReviewResponse createReview(Long packageProductId, Long memberId, ReviewCreateRequest request) {
        // 패키지 상품 조회
        PackageProduct packageProduct = packageProductRepository.findByIdOrThrow(packageProductId);

        // 이미 리뷰가 존재하는지 확인
        if (reviewRepository.findByPackageProductIdAndMemberId(packageProductId, memberId).isPresent()) {
            throw new BaseCustomException(REVIEW_FOUND);
        }

        // Review 엔티티 생성
        Review review = request.toEntity(packageProduct, memberId);
        reviewRepository.save(review); // 리뷰 저장

        // ReviewImage 리스트 생성 및 저장
        List<ReviewImage> images = request.toReviewImages(review);
        reviewImageRepository.saveAll(images); // 이미지 리스트 저장

        return ReviewResponse.fromEntity(review, images); // 생성된 리뷰 응답 반환
    }
}
