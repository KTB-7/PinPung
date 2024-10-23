package com.ktb7.pinpung.dto;

import com.ktb7.pinpung.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private boolean loginSuccess;
    private User user;
    private String accessToken;
    private Integer expiresIn;
}
