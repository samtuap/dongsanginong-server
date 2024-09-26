package org.samtuap.inong.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.repository.PackageProductRepository;
import org.samtuap.inong.domain.product.service.ImageService;
import org.samtuap.inong.domain.review.dto.ReviewCreateRequest;
import org.samtuap.inong.domain.review.dto.ReviewListResponse;
import org.samtuap.inong.domain.review.dto.ReviewUpdateRequest;
import org.samtuap.inong.domain.review.entity.Review;
import org.samtuap.inong.domain.review.entity.ReviewImage;
import org.samtuap.inong.domain.review.repository.ReviewImageRepository;
import org.samtuap.inong.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.samtuap.inong.common.exceptionType.ReviewExceptionType.*;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final PackageProductRepository packageProductRepository;
    private final ReviewImageService reviewImageService;
    private final ImageService imageService;

    @Transactional
    public void createReview(Long packageProductId, Long memberId, ReviewCreateRequest request) {
        // 패키지 상품 조회
        PackageProduct packageProduct = packageProductRepository.findByIdOrThrow(packageProductId);

        // 이미 리뷰가 존재하는지 확인
        if (reviewRepository.findByPackageProductIdAndMemberId(packageProductId, memberId).isPresent()) {
            throw new BaseCustomException(REVIEW_ALREADY_EXIST);
        }

        // Review 엔티티 생성 및 저장
        Review review = request.toEntity(packageProduct, memberId);
        Review savedReview = reviewRepository.save(review); // 리뷰 저장

        List<String> imageUrls = imageService.extractImageUrls(request.imageUrls());

        reviewImageService.saveImages(savedReview, imageUrls);
    }

    @Transactional
    public void updateReview(Long reviewId, Long memberId, ReviewUpdateRequest request) {
        // 기존 리뷰 조회
        Review existingReview = reviewRepository.findByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new BaseCustomException(REVIEW_NOT_FOUND));

        Review updatedReview = ReviewUpdateRequest.toUpdatedEntity(request, existingReview);
        reviewRepository.save(updatedReview);

        // 기존 이미지 삭제
        List<String> oldImageUrls = reviewImageService.findAllImageUrlsByReview(existingReview);
        reviewImageService.deleteImages(existingReview, oldImageUrls);

        // 새로운 이미지 저장
        List<String> newImageUrls = imageService.extractImageUrls(request.imageUrls());
        reviewImageService.saveImages(existingReview, newImageUrls);
    }

    @Transactional
    public void deleteReviewByMember(Long reviewId, Long memberId) {
        Review review = reviewRepository.findByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new BaseCustomException(REVIEW_NOT_FOUND));

        deleteReviewWithImages(review);
    }

    @Transactional
    public void deleteReviewBySeller(Long reviewId, Long sellerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseCustomException(REVIEW_NOT_FOUND));

        PackageProduct packageProduct = review.getPackageProduct();
        Farm farm = packageProduct.getFarm();

        // sellerId가 일치하지 않으면 예외 발생
        if (!farm.getSellerId().equals(sellerId)) {
            throw new BaseCustomException(AUTHORITY_NOT_FOUND);
        }
        deleteReviewWithImages(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> getReviewsByPackageProductId(Long packageProductId) {
        List<Review> reviews = reviewRepository.findAllByPackageProductId(packageProductId);
        return reviews.stream()
                .map(review -> {
                    // 리뷰에 연결된 이미지들 조회
                    List<ReviewImage> images = reviewImageRepository.findAllByReviewId(review.getId());
                    return ReviewListResponse.fromEntity(review, images);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReviewWithImages(Review review) {
        List<String> imageUrls = reviewImageService.findAllImageUrlsByReview(review);
        imageService.deleteImagesFromS3(imageUrls);
        reviewImageRepository.deleteAllByReviewId(review.getId());
        reviewRepository.delete(review);
    }
}
