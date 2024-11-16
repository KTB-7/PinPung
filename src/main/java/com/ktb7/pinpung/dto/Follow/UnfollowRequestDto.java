package com.ktb7.pinpung.dto.Follow;

import lombok.Getter;

@Getter
public class UnfollowRequestDto {
    private Long userId;
    private Long wantsToUnfollowId;
}