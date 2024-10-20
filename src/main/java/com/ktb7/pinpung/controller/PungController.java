package com.ktb7.pinpung.controller;

import com.amazonaws.services.ec2.model.Image;
import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.service.PungService;
import com.ktb7.pinpung.service.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/pungs")
public class PungController {
    private final PungService pungService;

    @GetMapping("/{placeId}")
    public ResponseEntity<PungsResponseDto> getPungs(
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "0") int page, // page 쿼리 파라미터, 기본값 0
            @RequestParam(defaultValue = "3") int size  // size는 3으로 고정 (한 페이지에 3개의 펑)
    ) {
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
        try {
            pungService.uploadPung(userId, placeId, imageWithText, pureImage, text);
            return ResponseEntity.ok("Pung upload success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Pung upload failed: " + e.getMessage());
        }
    }
}
