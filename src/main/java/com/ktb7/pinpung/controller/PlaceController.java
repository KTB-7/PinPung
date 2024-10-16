package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PlaceInfoResponseDto;
import com.ktb7.pinpung.dto.PlaceNearbyResponseDto;
import com.ktb7.pinpung.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<PlaceNearbyResponseDto>> getPlacesWithRepresentativeImage(
            @RequestParam List<String> placeIds) {
        List<PlaceNearbyResponseDto> places = placeService.getPlacesWithRepresentativeImage(placeIds);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceInfoResponseDto> getPlaceInfo(@PathVariable String placeId) {
        PlaceInfoResponseDto placeInfo = placeService.getPlaceInfo(placeId);
        return ResponseEntity.ok(placeInfo);
    }
}
