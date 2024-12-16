package com.ktb7.pinpung.dto.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class SearchPlaceInfoDto {
    private Long placeId;
    private String placeName;
    private String address;
    private Boolean hasPung;
    private Boolean byFriend;
    private Long imageId;
    private List<String> tags;
    private Long reviewCount;
    private String x;
    private String y;
}
