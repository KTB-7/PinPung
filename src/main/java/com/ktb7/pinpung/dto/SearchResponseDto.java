package com.ktb7.pinpung.dto;

import java.util.List;

public class SearchResponseDto {

    private Integer placeId;
    private List<String> tags;
    private Long reviewCount;

    public SearchResponseDto(Integer placeId, List<String> tags, Long reviewCount) {
        this.placeId = placeId;
        this.tags = tags;
        this.reviewCount = reviewCount;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }
}
