package com.ktb7.pinpung.dto.AI;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TrendingTagsAIResponseDto {
    private List<String> hashtags;
    private List<List<Long>> cafe_list;
}

