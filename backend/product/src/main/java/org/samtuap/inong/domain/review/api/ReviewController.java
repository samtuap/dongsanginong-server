package org.samtuap.inong.domain.review.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.review.dto.ReviewCreateRequest;
import org.samtuap.inong.domain.review.dto.ReviewResponse;
import org.samtuap.inong.domain.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{packageProductId}/create/{memberId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long packageProductId,
            @PathVariable Long memberId,
            @RequestBody ReviewCreateRequest request) {

        ReviewResponse response = reviewService.createReview(packageProductId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
