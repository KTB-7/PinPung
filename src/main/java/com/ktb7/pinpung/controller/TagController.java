package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Place.SimplePlaceDto;
import com.ktb7.pinpung.oauth2.service.TokenService;
import com.ktb7.pinpung.service.PlaceService;
import com.ktb7.pinpung.service.TagService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@Slf4j
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TokenService tokenService;
    private final PlaceService placeService;

    @GetMapping
    public ResponseEntity<List<String>> getTags(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        log.info("Received request to /tags");

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        ValidationUtils.validateUserId(userId);

        List<String> response = tagService.getTags();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/cafeList")
    public ResponseEntity<List<SimplePlaceDto>> getTag(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String tagName,
            @RequestParam String swLng,
            @RequestParam String swLat,
            @RequestParam String neLng,
            @RequestParam String neLat
    ) {
        log.info("Received request to /tags/tagname{}", tagName);

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateKeyword(tagName);
        ValidationUtils.validateRect(swLng, swLat, neLng, neLat);

        List<Long> placeIds = placeService.categorySearch("카페", swLng, swLat, neLng, neLat, null, null, "accuracy");
        List<SimplePlaceDto> places = tagService.getPlacesFromTag(tagName, placeIds);

        return ResponseEntity.ok(places);


    }
}
