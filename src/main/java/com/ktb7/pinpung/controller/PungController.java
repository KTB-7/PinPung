package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.PungService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
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
        // 유효성 검증: placeId가 null인지 확인
        if (placeId == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, ErrorCode.INVALID_PARAMETER.getMsg());
        }

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
        // 유효성 검증: 필수 파라미터 확인
        if (userId == null || placeId == null || imageWithText == null || pureImage == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER, ErrorCode.MISSING_PARAMETER.getMsg());
        }

        try {
            pungService.uploadPung(userId, placeId, imageWithText, pureImage, text);
            return ResponseEntity.ok("Pung upload success");
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, ErrorCode.FILE_UPLOAD_FAILED.getMsg());
        }
    }
}
