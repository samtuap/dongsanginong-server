package org.samtuap.inong.domain.review.dto;

import org.samtuap.inong.domain.review.entity.Review;
import org.samtuap.inong.domain.review.entity.ReviewImage;

import java.util.List;
import java.util.stream.Collectors;

public record ReviewUpdateRequest(
        String title,
        Integer rating,
        String contents,
        List<String> imageUrls
) {
    // 새로운 리뷰 엔티티를 생성
    public static Review toUpdatedEntity(ReviewUpdateRequest dto,Review review) {
        return Review.builder()
                .id(review.getId())
                .packageProduct(review.getPackageProduct())
                .memberId(review.getMemberId())
                .title(dto.title)
                .rating(dto.rating)
                .contents(dto.contents)
                .build();
    }

    public List<ReviewImage> toReviewImages(Review review) {
        return this.imageUrls.stream()
                .map(url -> ReviewImage.builder()
                        .review(review)
                        .imageUrl(url)
                        .build())
                .collect(Collectors.toList());
    }
}
