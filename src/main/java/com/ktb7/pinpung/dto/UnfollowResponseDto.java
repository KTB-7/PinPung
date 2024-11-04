package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnfollowResponseDto {
    private Long userId;
    private Long isNotFollowing; // 언팔로우된 유저 ID
}
