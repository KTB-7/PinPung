package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.SearchResponseDto;
import com.ktb7.pinpung.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/places")
    public ResponseEntity<List<SearchResponseDto>> getPlacesWithReviewCountsAndTags(
            @RequestParam List<String> placeIds) {
        List<SearchResponseDto> places = searchService.getPlacesWithReviewCountsAndTags(placeIds);
        return ResponseEntity.ok(places);
    }
}
