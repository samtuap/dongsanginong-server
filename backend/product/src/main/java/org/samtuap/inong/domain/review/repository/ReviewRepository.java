package org.samtuap.inong.domain.review.repository;

import org.samtuap.inong.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByPackageProductIdAndMemberId(Long packageProductId, Long memberId);

    Optional<Review> findByIdAndMemberId(Long id, Long memberId);

    List<Review> findAllByPackageProductId(Long packageProductId);

}
