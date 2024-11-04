package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.ReviewService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadReviews(
            @RequestParam Long userId,
            @RequestParam Long placeId,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String text
    ) {
        // 로그 출력
        log.info("uploadReviews: {} {} {}", userId, placeId, text);

        // 유효성 검증
        ValidationUtils.validateUserAndPlaceId(userId, placeId);
//        ValidationUtils.validateFile(image, "image");

        try {
            reviewService.uploadReview(userId, placeId, image, text);
            return ResponseEntity.ok("Review upload success");
        } catch (Exception e) {
            log.error("Review upload failed: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, ErrorCode.FILE_UPLOAD_FAILED.getMsg());
        }
    }


//    @PatchMapping("/modify")
//    public ResponseEntity<String> modifyReviews() {
//
//        // 유저아이디와 액세스토큰으로 받은 값 일치하는지(수정해도 되는 사람인지) 확인
//        // 유저 아이디, 텍스트, 이미지로 구성(placeid는 수정불가)
//        // userid, placeid, image, text를 받음
//        // 해당 리뷰가 존재하는지 확인
//        // 텍스트, 이미지를 가지고 변경
//
//    }
}
