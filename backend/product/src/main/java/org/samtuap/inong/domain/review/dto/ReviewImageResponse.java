package org.samtuap.inong.domain.review.dto;

import org.samtuap.inong.domain.review.entity.ReviewImage;

public record ReviewImageResponse(
        Long id,
        String imageUrl
) {
    public static ReviewImageResponse fromEntity(ReviewImage reviewImage) {
        return new ReviewImageResponse(
                reviewImage.getId(),
                reviewImage.getImageUrl()
        );
    }
}

