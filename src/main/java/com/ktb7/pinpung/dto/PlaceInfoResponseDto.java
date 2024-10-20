package com.ktb7.pinpung.dto;

import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Review;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PlaceInfoResponseDto {
    private Long placeId;
    private String placeName;
    private String address;
    private List<String> tags;
    private List<Review> reviews;
    private Pung representativePung;

    // for test

    public Long getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public Pung getRepresentativePung() {
        return representativePung;
    }
}
