package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.dto.Place.PlaceNearbyResponseDto;
import com.ktb7.pinpung.service.PlaceService;
import com.ktb7.pinpung.service.SearchService;
import com.ktb7.pinpung.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final PlaceService placeService;

    @GetMapping
    @Operation(
            summary = "키워드로 검색하기",
            description = "사용자가 입력한 키워드로 적절한 카페를 검색합니다."
    )
    public void getPlacesWithRepresentativeImage(
            @RequestParam String keyword,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat) {

        log.info("Received request to /nearby with keyword {}, SW({},{}) and NE({},{})", keyword, swLng, swLat, neLng, neLat);

        // 유효성 검증
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);
        ValidationUtils.validateKeyword(keyword);

        Boolean haveLocation = searchService.useGpt(keyword);
        List<Long> placeIdList;

        if (haveLocation) {
            // rect 없이 요청 보내기
            placeIdList = placeService.categorySearch(keyword, null, null, null, null);
        } else {
            // rect 포함하여 요청 보내기
            placeIdList = placeService.categorySearch(keyword, swLng, swLat, neLng, neLat);
        }

        log.info("placeIdList {}: ", placeIdList);
        // placeidlist 없을 때에 대한 예외처리
        // placeidlist 인공지능으로 넘기기
    }
}
