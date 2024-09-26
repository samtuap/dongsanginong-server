package org.samtuap.inong.domain.review.repository;

import org.samtuap.inong.domain.review.entity.Review;
import org.samtuap.inong.domain.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    void deleteAllByReviewId(Long reviewId);
    List<ReviewImage> findAllByReviewId(Long reviewId);

    void deleteByReviewAndImageUrl(Review review, String imageUrl);
}
