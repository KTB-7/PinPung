package com.ktb7.pinpung.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DefaultProfileDto {
    private Long userId;
    private String userName;
    private Integer followerCount;
    private Integer followingCount;
    private Long pungCount;
    private Long reviewCount;
}
