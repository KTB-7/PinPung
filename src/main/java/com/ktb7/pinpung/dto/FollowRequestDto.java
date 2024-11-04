package com.ktb7.pinpung.dto;

import lombok.Getter;

@Getter
public class FollowRequestDto {
    private Long userId;
    private Long wantsToFollowId;
}