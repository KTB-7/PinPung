package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class SimpleUserDto {
    private Long userId;
    private String username;
//    private MultipartFile profileImage;
}
