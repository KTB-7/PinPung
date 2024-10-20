package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SearchResponseDto {

    private Long placeId;
    private List<String> tags;
    private Long reviewCount;

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
