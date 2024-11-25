package com.ktb7.pinpung.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DefaultProfileDto {
    private Long userId;
    private String userName;
    private Integer followerCount;
    private Integer followingCount;
}
