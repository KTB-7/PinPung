package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.dto.Place.PlaceNearbyResponseDto;
import com.ktb7.pinpung.dto.Search.SearchPlaceInfoDto;
import com.ktb7.pinpung.dto.Search.SearchResponseDto;
import com.ktb7.pinpung.dto.Search.SearchTagReviewDto;
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

    @GetMapping("/map")
    @Operation(
            summary = "키워드로 검색하기",
            description = "사용자가 입력한 키워드로 적절한 카페를 검색합니다."
    )
    public ResponseEntity<PlaceNearbyResponseDto> searchInMap(
            @RequestParam Long userId,
            @RequestParam String keyword,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat
    ) {

        log.info("Received request to /search/map with keyword {}, SW({},{}) and NE({},{})", keyword, swLng, swLat, neLng, neLat);

        // 유효성 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);
        ValidationUtils.validateKeyword(keyword);

        Boolean haveLocation = searchService.useGpt(keyword);
        List<Long> placeIdListForMap;

        if (haveLocation) {
            // rect 없이 요청 보내기
            placeIdListForMap = placeService.categorySearch(keyword, null, null, null, null, null, null);
        } else {
            // rect 포함하여 요청 보내기
            placeIdListForMap = placeService.categorySearch(keyword, swLng, swLat, neLng, neLat, null, null);
        }

        log.info("placeIdListCount {}: ", placeIdListForMap.size());
        // placeidlist 없을 때에 대한 예외처리
        // placeIdList 정확도순 정렬됨

        List<PlaceNearbyDto> placeNearbyInfoList = placeService.getPlacesWithRepresentativeImage(placeIdListForMap);

        PlaceNearbyResponseDto response = new PlaceNearbyResponseDto(placeNearbyInfoList.size(), placeNearbyInfoList);
        log.info("Nearby places count: {}", response.getCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/accuracy")
    @Operation(
            summary = "키워드로 검색하기",
            description = "사용자가 입력한 키워드로 적절한 카페를 검색합니다."
    )
    public ResponseEntity<SearchResponseDto> searchWithAccuracy(
            @RequestParam Long userId,
            @RequestParam String keyword,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat
    ) {

        log.info("Received request to /search/accuracy with keyword {}, SW({},{}) and NE({},{})", keyword, swLng, swLat, neLng, neLat);

        // 유효성 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);
        ValidationUtils.validateKeyword(keyword);

        Boolean haveLocation = searchService.useGpt(keyword);
        List<Long> placeIdListWithAccuracy;

        if (haveLocation) {
            // rect 없이 요청 보내기
            placeIdListWithAccuracy = placeService.categorySearch(keyword, null, null, null, null, null, null);
        } else {
            // rect 포함하여 요청 보내기
            placeIdListWithAccuracy = placeService.categorySearch(keyword, swLng, swLat, neLng, neLat, null, null);
        }

        log.info("placeIdListCount {}: ", placeIdListWithAccuracy.size());
        // placeidlist 없을 때에 대한 예외처리
        // placeIdList 정확도순 정렬됨

        List<PlaceNearbyDto> placeNearbyInfoList = placeService.getPlacesWithRepresentativeImage(placeIdListWithAccuracy);
        List<SearchTagReviewDto> placeNearbyTagReviewList = searchService.getPlacesWithReviewCountsAndTags(placeIdListWithAccuracy);

        SearchResponseDto response = searchService.makeResponse(userId, placeNearbyInfoList, placeNearbyTagReviewList, "accuracy");
        log.info("SearchResponseDtoCount: {}",  response.getCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/distance")
    @Operation(
            summary = "키워드로 검색하기",
            description = "사용자가 입력한 키워드로 적절한 카페를 검색합니다."
    )
    public ResponseEntity<SearchResponseDto> searchWithDistance(
            @RequestParam Long userId,
            @RequestParam String keyword,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat,
            @RequestParam String x,
            @RequestParam String y
    ) {

        log.info("Received request to /search/distance with keyword {}, SW({},{}) and NE({},{})", keyword, swLng, swLat, neLng, neLat);

        // 유효성 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateCoordinates(x, y);
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);
        ValidationUtils.validateKeyword(keyword);

        Boolean haveLocation = searchService.useGpt(keyword);
        List<Long> placeIdListWithDistance;

        if (haveLocation) {
            // rect 없이 요청 보내기
            placeIdListWithDistance = placeService.categorySearch(keyword, null, null, null, null, x, y);
        } else {
            // rect 포함하여 요청 보내기
            placeIdListWithDistance = placeService.categorySearch(keyword, swLng, swLat, neLng, neLat, x, y);
        }

        log.info("placeIdListCount {}: ", placeIdListWithDistance.size());
        // placeidlist 없을 때에 대한 예외처리
        // 거리순 정렬된 상태

        List<PlaceNearbyDto> placeNearbyInfoList = placeService.getPlacesWithRepresentativeImage(placeIdListWithDistance);
        List<SearchTagReviewDto> placeNearbyTagReviewList = searchService.getPlacesWithReviewCountsAndTags(placeIdListWithDistance);

        SearchResponseDto response = searchService.makeResponse(userId, placeNearbyInfoList, placeNearbyTagReviewList, "distance");
        log.info("SearchResponseDtoCount: {}", response.getCount());
        return ResponseEntity.ok(response);
    }
}
