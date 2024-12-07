package com.ktb7.pinpung.dto.AI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TrendingTagsResponseDto {
    private Integer count;
    private List<PlacesPerTagDto> placesPerTags;
}
