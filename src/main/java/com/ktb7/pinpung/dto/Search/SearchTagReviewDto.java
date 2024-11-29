package com.ktb7.pinpung.dto.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class SearchTagReviewDto {
    private Long placeId;
    private List<String> tags;
    private Long reviewCount;
}
