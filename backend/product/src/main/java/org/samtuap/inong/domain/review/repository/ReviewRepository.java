package org.samtuap.inong.domain.review.repository;

import org.samtuap.inong.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByPackageProductIdAndMemberId(Long packageProductId, Long memberId);
}
