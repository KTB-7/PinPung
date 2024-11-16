package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Place.PlaceInfoResponseDto;
import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.dto.Place.PlaceNearbyResponseDto;
import com.ktb7.pinpung.dto.Place.SearchResponseDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.PlaceService;
import com.ktb7.pinpung.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/places")
@Tag(name = "Places API", description = "장소 관련 API")
@AllArgsConstructor
public class PlaceController {
    private final PlaceService placeService;


    @GetMapping("/nearby")
    @Operation(
            summary = "지도에 카페 혹은 펑 마커 띄우기",
            description = "주어진 좌표(x, y)와 반경(radius)을 기준으로 주변 카페를 검색합니다.",
            parameters = {
                    @Parameter(name = "x", description = "사용자의 현재 위치 X좌표", required = true, example = "126.9780"),
                    @Parameter(name = "y", description = "사용자의 현재 위치 Y좌표", required = true, example = "37.5665"),
                    @Parameter(name = "radius", description = "검색 반경 (미터)", required = true, example = "100")
            }
    )
    public ResponseEntity<PlaceNearbyResponseDto> getPlacesWithRepresentativeImage(
            @RequestParam String x, @RequestParam String y, @RequestParam Integer radius) {

        log.info("Received request to /nearby with x={}, y={}, radius={}", x, y, radius);

        // 유효성 검증
        ValidationUtils.validateCoordinates(x, y);
        ValidationUtils.validateRadius(radius);

        List<Long> placeIds = placeService.categorySearch(x, y, radius);
        List<PlaceNearbyDto> places = placeService.getPlacesWithRepresentativeImage(placeIds);
        PlaceNearbyResponseDto response = new PlaceNearbyResponseDto(places.size(), places);
        log.info("{}", response.getCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{placeId}")
    @Operation(
            summary = "특정 장소의 상세 정보 조회",
            description = "장소 ID(placeId)를 사용하여 장소의 상세 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "placeId", description = "조회할 장소의 ID", required = true, example = "123")
            }
    )
    public ResponseEntity<PlaceInfoResponseDto> getPlaceInfo(@PathVariable Long placeId) {
        log.info("Received request to /{placeId} with :{}", placeId);

        // 유효성 검증
        ValidationUtils.validatePlaceId(placeId);

        PlaceInfoResponseDto placeInfo = placeService.getPlaceInfo(placeId);
        if (placeInfo == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND, ErrorCode.PLACE_NOT_FOUND.getMsg());
        }
        return ResponseEntity.ok(placeInfo);
    }

    @GetMapping("/tags-reviews")
    @Operation(
            summary = "검색 결과별 태그 및 리뷰 조회",
            description = "여러 장소별 태그 및 리뷰 개수를 조회합니다.",
            parameters = {
                    @Parameter(name = "placeIds", description = "조회할 장소 ID 리스트", required = true, example = "[123, 456, 789]")
            }
    )
    public ResponseEntity<List<SearchResponseDto>> getPlacesWithReviewCountsAndTags(
            @RequestParam List<Long> placeIds) {
        log.info("Received request to /tag-reviews with :{}", placeIds);

        // 유효성 검증
        ValidationUtils.validatePlaceIds(placeIds);

        List<SearchResponseDto> places = placeService.getPlacesWithReviewCountsAndTags(placeIds);
        return ResponseEntity.ok(places);
    }
}
