package com.ktb7.pinpung.dto.Pung;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PungDto {
    private Long pungId;
    private Long userId;
    private String userName;
    private Long imageId;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
