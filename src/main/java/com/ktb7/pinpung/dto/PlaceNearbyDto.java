package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceNearbyDto {
    private Long placeId;
    private String placeName;
    private Boolean hasPung;
    private Long imageWithText;
    private String x;
    private String y;
}
