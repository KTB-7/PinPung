package com.ktb7.pinpung.dto.Place;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SimplePlaceDto {
    private Long placeId;
    private String placeName;
    private String address;
    private List<String> tags;
    private Long imageId;
    private String x;
    private String y;
}
