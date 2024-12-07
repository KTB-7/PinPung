package com.ktb7.pinpung.dto.Place;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimplePlaceDto {
    private Long placeId;
    private String placeName;
    private String x;
    private String y;
}
