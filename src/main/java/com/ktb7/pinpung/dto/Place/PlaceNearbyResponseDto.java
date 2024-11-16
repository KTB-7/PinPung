package com.ktb7.pinpung.dto.Place;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaceNearbyResponseDto {
    private Integer count;
    private List<PlaceNearbyDto> places;
}
