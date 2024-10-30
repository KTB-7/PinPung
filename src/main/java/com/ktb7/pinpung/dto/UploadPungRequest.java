package com.ktb7.pinpung.dto;

import lombok.Getter;

@Getter
public class UploadPungRequest {
    private Long userId;
    private Long placeId;
    private String text;

}
