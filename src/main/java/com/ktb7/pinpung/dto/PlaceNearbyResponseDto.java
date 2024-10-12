package com.ktb7.pinpung.dto;

import java.util.List;

public class PlaceNearbyResponseDto {
    private String placeId;
    private String imageUrl;

    public PlaceNearbyResponseDto(String placeId, String imageUrl) {
        this.placeId = placeId;
        this.imageUrl = imageUrl;
    }

    // for test
    public String getPlaceId() {
        return placeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
