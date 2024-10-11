package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.PlaceNearbyResponseDto;
import com.ktb7.pinpung.dto.SearchResponseDto;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
