package com.ktb7.pinpung.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class SimplePung {
    private Long pungId;
    private Long imageId;
    private LocalDateTime updatedAt;
}
