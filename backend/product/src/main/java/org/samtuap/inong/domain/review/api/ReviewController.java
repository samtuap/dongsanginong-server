package org.samtuap.inong.domain.review.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.review.dto.ReviewCreateRequest;
import org.samtuap.inong.domain.review.dto.ReviewResponse;
import org.samtuap.inong.domain.review.dto.ReviewUpdateRequest;
import org.samtuap.inong.domain.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{packageProductId}/create")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long packageProductId,
            @RequestHeader("myId") Long memberId,
            @RequestBody ReviewCreateRequest request) {

        ReviewResponse response = reviewService.createReview(packageProductId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reviewId}/update")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestHeader("myId") Long memberId,
            @RequestBody ReviewUpdateRequest request) {

        ReviewResponse response = reviewService.updateReview(reviewId, memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{reviewId}/delete")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("myId") Long memberId) {
        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
