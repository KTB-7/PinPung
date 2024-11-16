package com.ktb7.pinpung.dto.Pung;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadPungRequestDto {
    private Long userId;
    private Long placeId;
    private MultipartFile imageWithText;
    private MultipartFile pureImage;
    private String text;
}
