package org.samtuap.inong.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.S3.ImageService;
import org.samtuap.inong.domain.review.entity.Review;
import org.samtuap.inong.domain.review.entity.ReviewImage;
import org.samtuap.inong.domain.review.repository.ReviewImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;
    private final ImageService imageService;

    @Transactional
    public void saveImages(Review review, List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            // PackageProductImage 엔티티 생성 및 저장
            ReviewImage reviewImage = ReviewImage.builder()
                    .imageUrl(imageUrl)
                    .review(review)
                    .build();
            reviewImageRepository.save(reviewImage);
        }
    }

    @Transactional
    public List<String> findAllImageUrlsByReview(Review review) {
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewId(review.getId());
        List<String> imageUrls = new ArrayList<>();
        for (ReviewImage reviewImage : reviewImages) {
            imageUrls.add(reviewImage.getImageUrl());
        }
        return imageUrls;
    }

    @Transactional
    public void deleteImages(Review review, List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            reviewImageRepository.deleteByReviewAndImageUrl(review, imageUrl);
        }
        imageService.deleteImagesFromS3(imageUrls);
    }
}
