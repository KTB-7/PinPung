package com.ktb7.pinpung.dto;

import com.ktb7.pinpung.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    public boolean loginSuccess;
    public User user;
}
