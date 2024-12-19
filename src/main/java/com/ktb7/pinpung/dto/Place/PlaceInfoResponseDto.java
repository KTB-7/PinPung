package com.ktb7.pinpung.dto.Place;

import com.ktb7.pinpung.dto.Pung.PungDto;
import com.ktb7.pinpung.dto.Review.ReviewsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaceInfoResponseDto {
    private Long placeId;
    private String placeName;
    private String address;
    private String x;
    private String y;
    private List<String> tags;
    private ReviewsDto reviews;
    private PungDto representativePung;
}
