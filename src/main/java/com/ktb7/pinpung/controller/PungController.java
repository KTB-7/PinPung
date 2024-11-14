package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.dto.UploadPungRequestDto;
import com.ktb7.pinpung.dto.UploadPungResponseDto;
import com.ktb7.pinpung.service.PungService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/pungs")
@AllArgsConstructor
public class PungController {

    private final PungService pungService;

    @GetMapping("/{placeId}")
    public ResponseEntity<PungsResponseDto> getPungs(
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        // 유효성 검증
        ValidationUtils.validatePlaceId(placeId);
        ValidationUtils.validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        PungsResponseDto pungs = pungService.getPungsByPlaceId(placeId, pageable);
        return ResponseEntity.ok(pungs);
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadPungResponseDto> uploadPungs(@ModelAttribute UploadPungRequestDto uploadPungRequest) {
        log.info("uploadPungs: {} {} {}", uploadPungRequest.getUserId(), uploadPungRequest.getPlaceId(), uploadPungRequest.getText());

        // 유효성 검증
        ValidationUtils.validateUserAndPlaceId(uploadPungRequest.getUserId(), uploadPungRequest.getPlaceId());
        ValidationUtils.validateFile(uploadPungRequest.getImageWithText(), "imageWithText");
        ValidationUtils.validateFile(uploadPungRequest.getPureImage(), "pureImage");

        pungService.uploadPung(uploadPungRequest);
        return ResponseEntity.ok(new UploadPungResponseDto("Pung upload success"));
    }
}
