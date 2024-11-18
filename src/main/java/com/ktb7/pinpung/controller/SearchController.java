package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.dto.Place.PlaceNearbyResponseDto;
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

@RestController("/api/search")
@Slf4j
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

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

//        if (haveLocation) {
//
//        } else {
//
//        }
    }
}
