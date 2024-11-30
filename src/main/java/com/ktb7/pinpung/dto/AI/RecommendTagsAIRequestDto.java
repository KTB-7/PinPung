package com.ktb7.pinpung.dto.AI;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RecommendTagsAIRequestDto {
    private Long userId;
    private List<Long> placeIdList;

}
