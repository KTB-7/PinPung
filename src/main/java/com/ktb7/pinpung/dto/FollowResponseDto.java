package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowResponseDto {
    private Long userId;       // 팔로우 요청한 유저 ID
    private Long isFollowing;  // 팔로우된 유저 ID
}
