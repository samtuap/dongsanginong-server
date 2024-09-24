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
    public Review toUpdatedEntity(Review review) {
        return Review.builder()
                .id(review.getId())
                .packageProduct(review.getPackageProduct())
                .memberId(review.getMemberId())
                .title(this.title != null ? this.title : review.getTitle())
                .rating(this.rating != null ? this.rating : review.getRating())
                .contents(this.contents != null ? this.contents : review.getContents())
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
