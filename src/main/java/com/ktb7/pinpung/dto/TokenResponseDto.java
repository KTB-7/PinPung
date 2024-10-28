package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenResponseDto {
    private Long userId;
    private String accessToken;
}
