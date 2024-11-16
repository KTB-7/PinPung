package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Review.UploadReviewRequestDto;
import com.ktb7.pinpung.dto.Review.ModifyReviewRequestDto;
import com.ktb7.pinpung.dto.Review.DeleteReviewRequestDto;
import com.ktb7.pinpung.dto.Review.ReviewResponseDto;
import com.ktb7.pinpung.service.ReviewService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Slf4j
@RequestMapping("/api/reviews")
@AllArgsConstructor
@Tag(name = "Review API", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/upload")
    @Operation(
            summary = "리뷰 업로드",
            description = "사용자가 작성한 리뷰를 업로드합니다.",
            parameters = {
                    @Parameter(name = "uploadReviewRequest", description = "리뷰 업로드 요청 데이터 (userId, placeId, text, 이미지)", required = true)
            }
    )
    public ResponseEntity<ReviewResponseDto> uploadReview(@ModelAttribute UploadReviewRequestDto uploadReviewRequest) {
        log.info("{}", uploadReviewRequest);
        log.info("uploadReview: {} {} {}", uploadReviewRequest.getUserId(), uploadReviewRequest.getPlaceId(), uploadReviewRequest.getText());

        ValidationUtils.validateUserAndPlaceId(uploadReviewRequest.getUserId(), uploadReviewRequest.getPlaceId());

        ReviewResponseDto response = reviewService.uploadReview(uploadReviewRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/modify")
    @Operation(
            summary = "리뷰 수정",
            description = "기존 리뷰 내용을 수정합니다.",
            parameters = {
                    @Parameter(name = "modifyReviewRequest", description = "리뷰 수정 요청 데이터 (userId, placeId, text, 이미지)", required = true)
            }
    )
    public ResponseEntity<ReviewResponseDto> modifyReview(@ModelAttribute ModifyReviewRequestDto modifyReviewRequest) {
        log.info("modifyReview: {} {} {}", modifyReviewRequest.getUserId(), modifyReviewRequest.getPlaceId(), modifyReviewRequest.getText());

        ValidationUtils.validateUserAndPlaceId(modifyReviewRequest.getUserId(), modifyReviewRequest.getPlaceId());

        ReviewResponseDto response = reviewService.modifyReview(modifyReviewRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "리뷰 삭제",
            description = "작성된 리뷰를 삭제합니다.",
            parameters = {
                    @Parameter(name = "deleteReviewRequest", description = "리뷰 삭제 요청 데이터 (userId, placeId, reviewId)", required = true)
            }
    )
    public ResponseEntity<ReviewResponseDto> deleteReview(@ModelAttribute DeleteReviewRequestDto deleteReviewRequest) {
        log.info("deleteReview: {} {} {}", deleteReviewRequest.getUserId(), deleteReviewRequest.getPlaceId(), deleteReviewRequest.getReviewId());

        ValidationUtils.validateUserAndPlaceId(deleteReviewRequest.getUserId(), deleteReviewRequest.getPlaceId());

        ReviewResponseDto response = reviewService.deleteReview(deleteReviewRequest);
        return ResponseEntity.ok(response);
    }
}
