package com.ktb7.pinpung.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoTokenInfoResponseDto {
    private Long id;
    private Integer expires_in;
    private Integer app_id;
}
