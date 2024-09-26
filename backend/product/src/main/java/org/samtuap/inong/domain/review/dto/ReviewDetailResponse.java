package org.samtuap.inong.domain.review.dto;

import lombok.Builder;
import org.samtuap.inong.domain.review.entity.Review;
import org.samtuap.inong.domain.review.entity.ReviewImage;

import java.util.List;

@Builder
public record ReviewDetailResponse(
        Long id,
        String title,
        Integer rating,
        String contents,
        List<String> imageUrls
) {
    public static ReviewDetailResponse fromEntity(Review review, List<ReviewImage> reviewImages) {
        List<String> imageUrls = reviewImages.stream()
                .map(ReviewImage::getImageUrl)
                .toList();

        return ReviewDetailResponse.builder()
                .id(review.getId())
                .title(review.getTitle())
                .rating(review.getRating())
                .contents(review.getContents())
                .imageUrls(imageUrls)
                .build();
    }
}