package com.ktb7.pinpung.dto;

import java.util.List;

public class SearchResponseDto {

    private Long placeId;
    private List<String> tags;
    private Long reviewCount;

    public SearchResponseDto(Long placeId, List<String> tags, Long reviewCount) {
        this.placeId = placeId;
        this.tags = tags;
        this.reviewCount = reviewCount;
    }

    // for test
    public Long getPlaceId() {
        return placeId;
    }

    public List<String> getTags() {
        return tags;
    }

    public Long getReviewCount() {
        return reviewCount;
    }

}
