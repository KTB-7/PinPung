package com.ktb7.pinpung.dto.Place;

import com.ktb7.pinpung.dto.Review.ReviewsDto;
import com.ktb7.pinpung.entity.Pung;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaceInfoResponseDto {
    private Long placeId;
    private String placeName;
    private String address;
    private List<String> tags;
    private ReviewsDto reviews;
    private Pung representativePung;
}
