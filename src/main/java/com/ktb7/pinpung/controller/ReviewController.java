package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.UploadReviewRequestDto;
import com.ktb7.pinpung.dto.ModifyReviewRequestDto;
import com.ktb7.pinpung.dto.DeleteReviewRequestDto;
import com.ktb7.pinpung.dto.ReviewResponseDto;
import com.ktb7.pinpung.service.ReviewService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/upload")
    public ResponseEntity<ReviewResponseDto> uploadReview(@ModelAttribute UploadReviewRequestDto uploadReviewRequest) {
        log.info("{}", uploadReviewRequest);
        log.info("uploadReview: {} {} {}", uploadReviewRequest.getUserId(), uploadReviewRequest.getPlaceId(), uploadReviewRequest.getText());

        ValidationUtils.validateUserAndPlaceId(uploadReviewRequest.getUserId(), uploadReviewRequest.getPlaceId());

        ReviewResponseDto response = reviewService.uploadReview(uploadReviewRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/modify")
    public ResponseEntity<ReviewResponseDto> modifyReview(@ModelAttribute ModifyReviewRequestDto modifyReviewRequest) {
        log.info("modifyReview: {} {} {}", modifyReviewRequest.getUserId(), modifyReviewRequest.getPlaceId(), modifyReviewRequest.getText());

        ValidationUtils.validateUserAndPlaceId(modifyReviewRequest.getUserId(), modifyReviewRequest.getPlaceId());

        ReviewResponseDto response = reviewService.modifyReview(modifyReviewRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ReviewResponseDto> deleteReview(@ModelAttribute DeleteReviewRequestDto deleteReviewRequest) {
        log.info("deleteReview: {} {} {}", deleteReviewRequest.getUserId(), deleteReviewRequest.getPlaceId(), deleteReviewRequest.getReviewId());

        ValidationUtils.validateUserAndPlaceId(deleteReviewRequest.getUserId(), deleteReviewRequest.getPlaceId());

        ReviewResponseDto response = reviewService.deleteReview(deleteReviewRequest);
        return ResponseEntity.ok(response);
    }
}
