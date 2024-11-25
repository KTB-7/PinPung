package com.ktb7.pinpung.dto.Pung;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PungResponseDto {
    private Long pungId;
    private Long userId;
    private Long placeId;
    private String placeName;
    private Long imageId;
    private LocalDateTime updatedAt;
}
