package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Place.*;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.PlaceService;
import com.ktb7.pinpung.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/places")
@Tag(name = "Places API", description = "장소 관련 API")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping("/nearby")
    @Operation(
            summary = "지도에 카페 혹은 펑 마커 띄우기",
            description = "주어진 좌표(SW, NE) 범위를 기준으로 주변 장소를 검색합니다."
    )
    public ResponseEntity<PlaceNearbyResponseDto> getPlacesWithRepresentativeImage(
            @RequestParam Long userId,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat) {

        log.info("Received request to /nearby with SW({},{}) and NE({},{})", swLng, swLat, neLng, neLat);

        // 유효성 검증
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);

        List<Long> placeIds = placeService.categorySearch("카페", swLng, swLat, neLng, neLat, null, null, "accuracy");
        List<PlaceNearbyDto> places = placeService.getPlacesWithRepresentativeImage(userId, placeIds);

        PlaceNearbyResponseDto response = new PlaceNearbyResponseDto(places.size(), places);
        log.info("Nearby places count: {}", response.getCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{placeId}")
    @Operation(
            summary = "특정 장소의 상세 정보 조회",
            description = "장소 ID(placeId)를 사용하여 장소의 상세 정보를 조회합니다."
    )
    public ResponseEntity<PlaceInfoResponseDto> getPlaceInfo(@PathVariable Long placeId) {
        log.info("Received request for place details with placeId: {}", placeId);

        // 유효성 검증
        ValidationUtils.validatePlaceId(placeId);

        PlaceInfoResponseDto placeInfo = placeService.getPlaceInfo(placeId);
        if (placeInfo == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND, "Place not found.");
        }

        log.info("Place details retrieved for placeId: {}", placeId);
        return ResponseEntity.ok(placeInfo);
    }

}
