package com.ktb7.pinpung.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String accessToken;
    private Integer expiresIn;

    public TokenResponseDto(String accessToken, Integer expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
