package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlaceNearbyResponseDto {
    private Long placeId;
    private String imageUrl;

    // for test
    public Long getPlaceId() {
        return placeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
