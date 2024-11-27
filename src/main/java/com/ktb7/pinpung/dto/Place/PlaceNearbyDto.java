package com.ktb7.pinpung.dto.Place;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceNearbyDto {
    private Long placeId;
    private String placeName;
    private Boolean hasPung;
    private Boolean byFriend;
    private Long imageId;
    private String x;
    private String y;
}
