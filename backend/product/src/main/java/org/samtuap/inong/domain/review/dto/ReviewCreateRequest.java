package org.samtuap.inong.domain.review.dto;

import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.review.entity.Review;
import org.samtuap.inong.domain.review.entity.ReviewImage;

import java.util.List;
import java.util.stream.Collectors;

public record ReviewCreateRequest(
        Long packageProductId,
        Long memberId,
        String title,
        Integer rating,
        String contents,
        List<String> imageUrls
) {
    public Review toEntity(PackageProduct packageProduct, Long memberId) {
        return Review.builder()
                .packageProduct(packageProduct)
                .memberId(memberId) // memberId를 여기서 설정
                .title(this.title)
                .rating(this.rating)
                .contents(this.contents)
                .build();
    }



    public List<ReviewImage> toReviewImages(Review review) {
        // ReviewImage 리스트 생성
        return this.imageUrls.stream()
                .map(url -> ReviewImage.builder() // Builder를 사용하여 생성
                        .review(review) // Review와의 관계 설정
                        .imageUrl(url)  // 이미지 URL 설정
                        .build())
                .collect(Collectors.toList());
    }
}
