package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.dto.UploadPungRequest;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.PungService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/pungs")
public class PungController {
    private final PungService pungService;

    public PungController(PungService pungService) {
        this.pungService = pungService;
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PungsResponseDto> getPungs(
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "0") int page, // page 쿼리 파라미터, 기본값 0
            @RequestParam(defaultValue = "3") int size  // size는 3으로 고정 (한 페이지에 3개의 펑)
    ) {
        // 유효성 검증
        ValidationUtils.validatePlaceId(placeId);
        ValidationUtils.validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        PungsResponseDto pungs = pungService.getPungsByPlaceId(placeId, pageable);
        return ResponseEntity.ok(pungs);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPungs(
            @RequestParam Long userId,
            @RequestParam Long placeId,
            @RequestParam MultipartFile imageWithText,
            @RequestParam MultipartFile pureImage,
            @RequestParam String text
    ) {
        // 로그 출력
        log.info("uploadPungs: {} {} {}", userId, placeId, text);

        // 유효성 검증
        ValidationUtils.validateUserAndPlaceId(userId, placeId);
        ValidationUtils.validateFile(imageWithText, "imageWithText");
        ValidationUtils.validateFile(pureImage, "pureImage");

        try {
            pungService.uploadPung(userId, placeId, imageWithText, pureImage, text);
            return ResponseEntity.ok("Pung upload success");
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, ErrorCode.FILE_UPLOAD_FAILED.getMsg());
        }
    }
}
