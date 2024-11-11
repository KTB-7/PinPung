package com.ktb7.pinpung.dto;

import lombok.Getter;

@Getter
public class UnfollowRequestDto {
    private Long userId;
    private Long wantsToUnfollowId;
}