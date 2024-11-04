package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.ReviewService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPungs(
            @RequestParam Long userId,
            @RequestParam Long placeId,
            @RequestParam MultipartFile image,
            @RequestParam String text
    ) {
        // 로그 출력
        log.info("uploadPungs: {} {} {}", userId, placeId, text);

        // 유효성 검증
        ValidationUtils.validateUserAndPlaceId(userId, placeId);
        ValidationUtils.validateFile(image, "image");

        try {
            reviewService.uploadReview(userId, placeId, image, text);
            return ResponseEntity.ok("Review upload success");
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, ErrorCode.FILE_UPLOAD_FAILED.getMsg());
        }
    }
}
