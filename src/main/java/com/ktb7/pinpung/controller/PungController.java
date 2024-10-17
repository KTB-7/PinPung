package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.service.PungService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pungs")
public class PungController {
    private final PungService pungService;

    public PungController(PungService pungService) {
        this.pungService = pungService;
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PungsResponseDto> getPungs(
            @PathVariable String placeId,
            @RequestParam(defaultValue = "0") int page, // page 쿼리 파라미터, 기본값 0
            @RequestParam(defaultValue = "3") int size  // size는 3으로 고정 (한 페이지에 3개의 펑)
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        PungsResponseDto pungs = pungService.getPungsByPlaceId(placeId, pageable);
        return ResponseEntity.ok(pungs);
    }
}
