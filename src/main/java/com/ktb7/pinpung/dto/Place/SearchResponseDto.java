package com.ktb7.pinpung.dto.Place;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class SearchResponseDto {

    private Long placeId;
    private List<String> tags;
    private Long reviewCount;

}
