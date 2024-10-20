package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PlaceNearbyResponseDto {
    private Long placeId;
    private Long imageId;

    // for test
    public Long getPlaceId() {
        return placeId;
    }
    public Long getImageId() {
        return imageId;
    }
}
