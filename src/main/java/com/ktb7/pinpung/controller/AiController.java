package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.AI.RecommendTagsAIResponseDto;
import com.ktb7.pinpung.dto.AI.RecommendTagsResponseDto;
import com.ktb7.pinpung.service.AiService;
import com.ktb7.pinpung.service.PlaceService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ai")
@Slf4j
public class AiController {

    private AiService aiService;
    private PlaceService placeService;

    @GetMapping("/recommend/{userId}")
    public ResponseEntity<RecommendTagsResponseDto> recommend(
            @PathVariable Long userId,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat,
            @RequestParam String x,
            @RequestParam String y
    ) {
        // 유효성 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateCoordinates(x, y);
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);

        List<Long> placeIdList = placeService.categorySearch("카페", swLng, swLat, neLng, neLat, x, y, "distance");
        RecommendTagsAIResponseDto recommendTagsAIResponse = aiService.recommend(userId, placeIdList);

        RecommendTagsResponseDto response = aiService.changeFormat(userId, recommendTagsAIResponse);

        return ResponseEntity.ok(response);

    }
}
