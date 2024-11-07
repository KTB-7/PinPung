package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.ReviewRequestDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.ReviewService;
import com.ktb7.pinpung.util.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadReviews(
            @RequestParam Long userId,
            @RequestParam Long placeId,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String text) {
        // 로그 출력
        log.info("uploadReviews: {} {} {}", userId, placeId, text);

        // 유효성 검증
        ValidationUtils.validateUserAndPlaceId(userId, placeId);
//        ValidationUtils.validateFile(image, "image");

        try {
            reviewService.uploadReview(userId, placeId, image, text);
            return ResponseEntity.ok("Review upload success");
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Review modify failed: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, ErrorCode.FILE_UPLOAD_FAILED.getMsg());
        }
    }


    @PatchMapping("/modify")
    public ResponseEntity<String> modifyReviews(
            @RequestParam Long userId,
            @RequestParam Long reviewId,
            @RequestParam Long placeId,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String text
    ) {
        // 로그 출력
        log.info("modifyReviews: {} {} {}", userId, placeId, text);

        // 유효성 검증
        ValidationUtils.validateUserAndPlaceId(userId, placeId);
//        ValidationUtils.validateReviewId(reviewId);
//        ValidationUtils.validateFile(image, "image");

        try {
            reviewService.modifyReview(userId, reviewId, placeId, image, text);
            return ResponseEntity.ok("Review modify success");
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Review modify failed: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, ErrorCode.FILE_UPLOAD_FAILED.getMsg());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReviews(
            @RequestParam Long userId,
            @RequestParam Long reviewId,
            @RequestParam Long placeId
    ) {
        // 로그 출력
        log.info("deleteReviews: {} {} {}", userId, placeId, reviewId);

        // 유효성 검증
        ValidationUtils.validateUserAndPlaceId(userId, placeId);

        try {
            reviewService.deleteReview(userId, reviewId, placeId);
            return ResponseEntity.ok("Review delete success");
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Review delete failed: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, ErrorCode.DATABASE_ERROR.getMsg());
        }
    }
}
