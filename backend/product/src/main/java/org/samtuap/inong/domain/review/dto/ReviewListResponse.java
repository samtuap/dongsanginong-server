package org.samtuap.inong.domain.review.dto;

import lombok.Builder;
import org.samtuap.inong.domain.review.entity.Review;
import org.samtuap.inong.domain.review.entity.ReviewImage;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReviewListResponse(
        Long id,
        String title,
        Integer rating,
        String contents,
        List<String> imageUrls,
        LocalDateTime updateAt,
        Long memberId
) {
    public static ReviewListResponse fromEntity(Review review, List<ReviewImage> reviewImages) {

        List<String> imageUrls = reviewImages.stream()
                .map(ReviewImage::getImageUrl) // ReviewImage 엔티티에서 이미지 URL 가져오기
                .toList();

        return ReviewListResponse.builder()
                .id(review.getId()) // 엔티티의 ID 가져오기
                .title(review.getTitle())
                .rating(review.getRating())
                .contents(review.getContents())
                .imageUrls(imageUrls)
                .updateAt(review.getUpdatedAt())
                .memberId(review.getMemberId())
                .build();
    }
}

