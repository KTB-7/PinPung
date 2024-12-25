package com.ktb7.pinpung.dto.AI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecommendTagsAIResponseDto {
    private List<Long> cafe_list;
}
