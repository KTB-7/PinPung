package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.dto.Place.PlaceNearbyResponseDto;
import com.ktb7.pinpung.dto.Search.SearchResponseDto;
import com.ktb7.pinpung.dto.Search.SearchTagReviewDto;
import com.ktb7.pinpung.service.PlaceService;
import com.ktb7.pinpung.service.SearchService;
import com.ktb7.pinpung.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Search API", description = "검색 관련 API")
public class SearchController {

    private final SearchService searchService;
    private final PlaceService placeService;

    @GetMapping("/map")
    @Operation(
            summary = "지도 기반 카페 검색",
            description = "키워드와 지도 영역을 기반으로 카페를 검색합니다.",
            parameters = {
                    @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1"),
                    @Parameter(name = "keyword", description = "검색 키워드", required = true, example = "카페"),
                    @Parameter(name = "swLng", description = "좌표 범위의 남서쪽 경도", required = true, example = "127.09903516290044"),
                    @Parameter(name = "swLat", description = "좌표 범위의 남서쪽 위도", required = true, example = "37.39051118703115"),
                    @Parameter(name = "neLng", description = "좌표 범위의 북동쪽 경도", required = true, example = "127.1039577504365"),
                    @Parameter(name = "neLat", description = "좌표 범위의 북동쪽 위도", required = true, example = "37.40618473206")
            }
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
            placeIdListForMap = placeService.categorySearch(keyword, null, null, null, null, null, null, "accuracy");
        } else {
            placeIdListForMap = placeService.categorySearch(keyword, swLng, swLat, neLng, neLat, null, null, "accuracy");
        }

        List<PlaceNearbyDto> placeNearbyInfoList = placeService.getPlacesWithRepresentativeImage(userId, placeIdListForMap);
        PlaceNearbyResponseDto response = new PlaceNearbyResponseDto(placeNearbyInfoList.size(), placeNearbyInfoList);
        log.info("Nearby places count: {}", response.getCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/accuracy")
    @Operation(
            summary = "키워드로 정확도 기반 검색",
            description = "키워드와 좌표 영역을 기반으로 정확도에 따라 정렬된 카페 리스트를 검색합니다.",
            parameters = {
                    @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1"),
                    @Parameter(name = "keyword", description = "검색 키워드", required = true, example = "카페"),
                    @Parameter(name = "swLng", description = "좌표 범위의 남서쪽 경도", required = true, example = "127.09903516290044"),
                    @Parameter(name = "swLat", description = "좌표 범위의 남서쪽 위도", required = true, example = "37.39051118703115"),
                    @Parameter(name = "neLng", description = "좌표 범위의 북동쪽 경도", required = true, example = "127.1039577504365"),
                    @Parameter(name = "neLat", description = "좌표 범위의 북동쪽 위도", required = true, example = "37.40618473206")
            }
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
            placeIdListWithAccuracy = placeService.categorySearch(keyword, null, null, null, null, null, null, "accuracy");
        } else {
            placeIdListWithAccuracy = placeService.categorySearch(keyword, swLng, swLat, neLng, neLat, null, null, "accuracy");
        }

        log.info("placeIdListCount {}: ", placeIdListWithAccuracy.size());
        // placeidlist 없을 때에 대한 예외처리
        // placeIdList 정확도순 정렬됨

        List<PlaceNearbyDto> placeNearbyInfoList = placeService.getPlacesWithRepresentativeImage(userId, placeIdListWithAccuracy);
        List<SearchTagReviewDto> placeNearbyTagReviewList = searchService.getPlacesWithReviewCountsAndTags(placeIdListWithAccuracy);

        SearchResponseDto response = searchService.makeResponse(userId, placeNearbyInfoList, placeNearbyTagReviewList, "accuracy");
        log.info("SearchResponseDtoCount: {}", response.getCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/distance")
    @Operation(
            summary = "키워드로 거리 기반 검색",
            description = "키워드와 좌표 영역을 기반으로 거리순으로 정렬된 카페 리스트를 검색합니다.",
            parameters = {
                    @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1"),
                    @Parameter(name = "keyword", description = "검색 키워드", required = true, example = "카페"),
                    @Parameter(name = "swLng", description = "좌표 범위의 남서쪽 경도", required = true, example = "127.09903516290044"),
                    @Parameter(name = "swLat", description = "좌표 범위의 남서쪽 위도", required = true, example = "37.39051118703115"),
                    @Parameter(name = "neLng", description = "좌표 범위의 북동쪽 경도", required = true, example = "127.1039577504365"),
                    @Parameter(name = "neLat", description = "좌표 범위의 북동쪽 위도", required = true, example = "37.40618473206"),
                    @Parameter(name = "x", description = "중심 경도", required = true, example = "127.101496456"),
                    @Parameter(name = "y", description = "중심 위도", required = true, example = "37.398342123")
            }
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
            placeIdListWithDistance = placeService.categorySearch(keyword, null, null, null, null, x, y, "distance");
        } else {
            placeIdListWithDistance = placeService.categorySearch(keyword, swLng, swLat, neLng, neLat, x, y, "distance");
        }

        log.info("placeIdListCount {}: ", placeIdListWithDistance.size());
        // placeidlist 없을 때에 대한 예외처리
        // 거리순 정렬된 상태

        List<PlaceNearbyDto> placeNearbyInfoList = placeService.getPlacesWithRepresentativeImage(userId, placeIdListWithDistance);
        List<SearchTagReviewDto> placeNearbyTagReviewList = searchService.getPlacesWithReviewCountsAndTags(placeIdListWithDistance);

        SearchResponseDto response = searchService.makeResponse(userId, placeNearbyInfoList, placeNearbyTagReviewList, "distance");
        log.info("SearchResponseDtoCount: {}", response.getCount());
        return ResponseEntity.ok(response);
    }
}
