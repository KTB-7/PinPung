package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Review.UploadReviewRequestDto;
import com.ktb7.pinpung.dto.Review.ModifyReviewRequestDto;
import com.ktb7.pinpung.dto.Review.DeleteReviewRequestDto;
import com.ktb7.pinpung.dto.Review.MessageResponseDto;
import com.ktb7.pinpung.oauth2.service.TokenService;
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
    private final TokenService tokenService;

    @PostMapping
    @Operation(
            summary = "리뷰 업로드",
            description = "사용자가 작성한 리뷰를 업로드합니다.",
            parameters = {
                    @Parameter(name = "uploadReviewRequest", description = "리뷰 업로드 요청 데이터 (userId, placeId, text, 이미지)", required = true)
            }
    )
    public ResponseEntity<MessageResponseDto> uploadReview(@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute UploadReviewRequestDto uploadReviewRequest) {
        log.info("uploadReview: {} {}", uploadReviewRequest.getPlaceId(), uploadReviewRequest.getText());

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        ValidationUtils.validateUserAndPlaceId(userId, uploadReviewRequest.getPlaceId());

        MessageResponseDto response = reviewService.uploadReview(userId, uploadReviewRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    @Operation(
            summary = "리뷰 수정",
            description = "기존 리뷰 내용을 수정합니다.",
            parameters = {
                    @Parameter(name = "modifyReviewRequest", description = "리뷰 수정 요청 데이터 (userId, placeId, text, 이미지)", required = true)
            }
    )
    public ResponseEntity<MessageResponseDto> modifyReview(@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute ModifyReviewRequestDto modifyReviewRequest) {
        log.info("modifyReview: {} {}", modifyReviewRequest.getPlaceId(), modifyReviewRequest.getText());

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        ValidationUtils.validateUserAndPlaceId(userId, modifyReviewRequest.getPlaceId());

        MessageResponseDto response = reviewService.modifyReview(userId, modifyReviewRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(
            summary = "리뷰 삭제",
            description = "작성된 리뷰를 삭제합니다.",
            parameters = {
                    @Parameter(name = "deleteReviewRequest", description = "리뷰 삭제 요청 데이터 (userId, placeId, reviewId)", required = true)
            }
    )
    public ResponseEntity<MessageResponseDto> deleteReview(@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute DeleteReviewRequestDto deleteReviewRequest) {
        log.info("deleteReview: {} {}", deleteReviewRequest.getPlaceId(), deleteReviewRequest.getReviewId());

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        ValidationUtils.validateUserAndPlaceId(userId, deleteReviewRequest.getPlaceId());

        MessageResponseDto response = reviewService.deleteReview(userId, deleteReviewRequest);
        return ResponseEntity.ok(response);
    }
}
