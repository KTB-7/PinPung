package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PlaceInfoResponseDto;
import com.ktb7.pinpung.dto.PlaceNearbyDto;
import com.ktb7.pinpung.dto.PlaceNearbyResponseDto;
import com.ktb7.pinpung.dto.SearchResponseDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.PlaceService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/places")
@AllArgsConstructor
public class PlaceController {
    private final PlaceService placeService;


    @GetMapping("/nearby")
    public ResponseEntity<PlaceNearbyResponseDto> getPlacesWithRepresentativeImage(
            @RequestParam String x, @RequestParam String y, @RequestParam Integer radius) {

        // 유효성 검증
        ValidationUtils.validateCoordinates(x, y);
        ValidationUtils.validateRadius(radius);

        List<Long> placeIds = placeService.categorySearch(x, y, radius);
        List<PlaceNearbyDto> places = placeService.getPlacesWithRepresentativeImage(placeIds);
        PlaceNearbyResponseDto response = new PlaceNearbyResponseDto(places.size(), places);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceInfoResponseDto> getPlaceInfo(@PathVariable Long placeId) {
        // 유효성 검증
        ValidationUtils.validatePlaceId(placeId);

        PlaceInfoResponseDto placeInfo = placeService.getPlaceInfo(placeId);
        if (placeInfo == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND, ErrorCode.PLACE_NOT_FOUND.getMsg());
        }
        return ResponseEntity.ok(placeInfo);
    }

    @GetMapping("/tags-reviews")
    public ResponseEntity<List<SearchResponseDto>> getPlacesWithReviewCountsAndTags(
            @RequestParam List<Long> placeIds) {
        // 유효성 검증
        ValidationUtils.validatePlaceIds(placeIds);

        List<SearchResponseDto> places = placeService.getPlacesWithReviewCountsAndTags(placeIds);
        return ResponseEntity.ok(places);
    }
}
