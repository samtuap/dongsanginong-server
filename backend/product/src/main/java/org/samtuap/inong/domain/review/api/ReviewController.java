package org.samtuap.inong.domain.review.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.review.dto.ReviewCreateRequest;
import org.samtuap.inong.domain.review.dto.ReviewDetailResponse;
import org.samtuap.inong.domain.review.dto.ReviewListResponse;
import org.samtuap.inong.domain.review.dto.ReviewUpdateRequest;
import org.samtuap.inong.domain.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{packageProductId}/create")
    public ResponseEntity<?> createReview(
            @PathVariable Long packageProductId,
            @RequestHeader("myId") Long memberId,
            @RequestBody ReviewCreateRequest request) {

        reviewService.createReview(packageProductId, memberId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}/update")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestHeader("myId") Long memberId,
            @RequestBody ReviewUpdateRequest request) {

        reviewService.updateReview(reviewId, memberId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}/delete")
    public ResponseEntity<?> deleteReviewByMember(
            @PathVariable Long reviewId,
            @RequestHeader("myId") Long memberId) {

        reviewService.deleteReviewByMember(reviewId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/seller/{reviewId}/delete")
    public ResponseEntity<?> deleteReviewBySeller(
            @PathVariable Long reviewId,
            @RequestHeader("sellerId") Long sellerId) {

        reviewService.deleteReviewBySeller(reviewId, sellerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("/no-auth/{packageProductId}/list")
    public ResponseEntity<List<ReviewListResponse>> ReviewList(@PathVariable Long packageProductId) {
        List<ReviewListResponse> reviews = reviewService.getReviewsByPackageProductId(packageProductId);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }


    @GetMapping("/no-auth/detail/{reviewId}")
    public ResponseEntity<ReviewDetailResponse> getReviewDetail(@PathVariable Long reviewId) {
        ReviewDetailResponse reviewDetail = reviewService.getReviewDetail(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(reviewDetail);
    }

}
