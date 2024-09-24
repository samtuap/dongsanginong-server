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
                .memberId(memberId)
                .title(this.title)
                .rating(this.rating)
                .contents(this.contents)
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
