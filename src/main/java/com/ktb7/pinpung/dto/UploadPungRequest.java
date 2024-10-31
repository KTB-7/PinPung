package com.ktb7.pinpung.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UploadPungRequest {
    private Long userId;
    private Long placeId;
    private String text;
}
