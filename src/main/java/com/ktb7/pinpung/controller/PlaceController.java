package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PlaceInfoResponseDto;
import com.ktb7.pinpung.dto.PlaceNearbyResponseDto;
import com.ktb7.pinpung.dto.SearchResponseDto;
import com.ktb7.pinpung.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<List<PlaceNearbyResponseDto>> getPlacesWithRepresentativeImage(
            @RequestParam List<Long> placeIds) {
        List<PlaceNearbyResponseDto> places = placeService.getPlacesWithRepresentativeImage(placeIds);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceInfoResponseDto> getPlaceInfo(@PathVariable Long placeId) {
        PlaceInfoResponseDto placeInfo = placeService.getPlaceInfo(placeId);
        return ResponseEntity.ok(placeInfo);
    }

    @GetMapping("/tags-reviews")
    public ResponseEntity<List<SearchResponseDto>> getPlacesWithReviewCountsAndTags(
            @RequestParam List<Long> placeIds) {
        if (placeIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<SearchResponseDto> places = placeService.getPlacesWithReviewCountsAndTags(placeIds);
        return ResponseEntity.ok(places);
    }

}
