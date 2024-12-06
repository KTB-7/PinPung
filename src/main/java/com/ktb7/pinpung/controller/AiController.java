package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.AI.RecommendTagsAIResponseDto;
import com.ktb7.pinpung.dto.AI.RecommendTagsResponseDto;
import com.ktb7.pinpung.oauth2.service.TokenService;
import com.ktb7.pinpung.service.AiService;
import com.ktb7.pinpung.service.PlaceService;
import com.ktb7.pinpung.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI API", description = "AI 태그 추천 및 트렌딩 태그 API")
@Slf4j
public class AiController {

    private final AiService aiService;
    private final PlaceService placeService;
    private final TokenService tokenService;

    @GetMapping("/recommend")
    @Operation(
            summary = "사용자 맞춤 태그 추천",
            description = "사용자 ID와 좌표 범위를 기반으로 AI가 맞춤 태그를 추천합니다.",
            parameters = {
                    @Parameter(name = "swLng", description = "좌표 범위의 남서쪽 경도", required = true, example = "127.09903516290044"),
                    @Parameter(name = "swLat", description = "좌표 범위의 남서쪽 위도", required = true, example = "37.39051118703115"),
                    @Parameter(name = "neLng", description = "좌표 범위의 북동쪽 경도", required = true, example = "127.1039577504365"),
                    @Parameter(name = "neLat", description = "좌표 범위의 북동쪽 위도", required = true, example = "37.40618473206"),
                    @Parameter(name = "x", description = "중심 경도", required = true, example = "127.101496456"),
                    @Parameter(name = "y", description = "중심 위도", required = true, example = "37.398342123")
            }
    )
    public ResponseEntity<RecommendTagsResponseDto> recommend(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat,
            @RequestParam String x,
            @RequestParam String y
    ) {
        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        // 유효성 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateCoordinates(x, y);
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);

        List<Long> placeIdList = placeService.categorySearch("카페", swLng, swLat, neLng, neLat, x, y, "distance");
        RecommendTagsAIResponseDto recommendTagsAIResponse = aiService.recommend(userId, placeIdList);

        RecommendTagsResponseDto response = aiService.changeFormat(userId, recommendTagsAIResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/trending")
    @Operation(
            summary = "트렌딩 태그 추천",
            description = "현재 인기 있는 트렌딩 태그를 추천합니다.",
            parameters = {
                    @Parameter(name = "swLng", description = "좌표 범위의 남서쪽 경도", required = true, example = "127.09903516290044"),
                    @Parameter(name = "swLat", description = "좌표 범위의 남서쪽 위도", required = true, example = "37.39051118703115"),
                    @Parameter(name = "neLng", description = "좌표 범위의 북동쪽 경도", required = true, example = "127.1039577504365"),
                    @Parameter(name = "neLat", description = "좌표 범위의 북동쪽 위도", required = true, example = "37.40618473206"),
                    @Parameter(name = "x", description = "중심 경도", required = true, example = "127.101496456"),
                    @Parameter(name = "y", description = "중심 위도", required = true, example = "37.398342123")
            }
    )
    public ResponseEntity<RecommendTagsResponseDto> trending(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat,
            @RequestParam String x,
            @RequestParam String y
    ) {
        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        // 유효성 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateCoordinates(x, y);
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);

        List<Long> placeIdList = placeService.categorySearch("카페", swLng, swLat, neLng, neLat, x, y, "distance");
        RecommendTagsAIResponseDto recommendTagsAIResponse = aiService.getTrending(userId, placeIdList);

        RecommendTagsResponseDto response = aiService.changeFormat(userId, recommendTagsAIResponse);

        return ResponseEntity.ok(response);
    }
}
