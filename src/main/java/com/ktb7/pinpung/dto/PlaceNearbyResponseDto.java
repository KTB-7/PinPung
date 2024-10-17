package com.ktb7.pinpung.dto;

import java.util.List;

public class PlaceNearbyResponseDto {
    private Long placeId;
    private String imageUrl;

    public PlaceNearbyResponseDto(Long placeId, String imageUrl) {
        this.placeId = placeId;
        this.imageUrl = imageUrl;
    }

    // for test
    public Long getPlaceId() {
        return placeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
