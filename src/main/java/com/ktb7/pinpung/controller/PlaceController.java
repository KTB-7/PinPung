package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PlaceInfoResponseDto;
import com.ktb7.pinpung.dto.PlaceNearbyDto;
import com.ktb7.pinpung.dto.PlaceNearbyResponseDto;
import com.ktb7.pinpung.dto.SearchResponseDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/nearby")
    public ResponseEntity<PlaceNearbyResponseDto> getPlacesWithRepresentativeImage(
            @RequestParam String x, @RequestParam String y, @RequestParam Integer radius) {
        // 유효성 검증: 각 값이 비어있지 않은지 확인?
        // 코드 추가


        // service.categotySearch
        // place service에서 카카오 로컬 api 써서 현재 위치로 검색 결과 얻어내기
        // 검색 결과에서 placeid만 추출하기

        // service.getPlacesWithRepresentativeImage
        // 추출한 placeid list로 펑 있나없나 확인(기존코드)
        // 펑 있으면 haspung true로 해두는 코드만 추가하면 됨, 그리고 각 장소의 xy값도 추가하기
        // 최종은 placenearbyresponsedto, 그 안에 pndto 들어가도록

        List<Long> placeIds = placeService.categorySearch(x, y, radius);
        List<PlaceNearbyDto> places = placeService.getPlacesWithRepresentativeImage(placeIds);
        PlaceNearbyResponseDto response = new PlaceNearbyResponseDto(places.size(), places);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceInfoResponseDto> getPlaceInfo(@PathVariable Long placeId) {
        // 유효성 검증: placeId가 null인지 확인, 리스트가 아닌지 확인
        if (placeId == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, ErrorCode.INVALID_PARAMETER.getMsg());
        }

        PlaceInfoResponseDto placeInfo = placeService.getPlaceInfo(placeId);
        if (placeInfo == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND, ErrorCode.PLACE_NOT_FOUND.getMsg());
        }
        return ResponseEntity.ok(placeInfo);
    }

    @GetMapping("/tags-reviews")
    public ResponseEntity<List<SearchResponseDto>> getPlacesWithReviewCountsAndTags(
            @RequestParam List<Long> placeIds) {
        // 유효성 검증: placeIds가 비어 있는지 확인
        if (placeIds == null || placeIds.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER, ErrorCode.MISSING_PARAMETER.getMsg());
        }

        List<SearchResponseDto> places = placeService.getPlacesWithReviewCountsAndTags(placeIds);
        return ResponseEntity.ok(places);
    }
}
