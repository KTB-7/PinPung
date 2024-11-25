package com.ktb7.pinpung.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class SimpleReview {
    private Long reviewId;
    private Long userId;
    private Long placeId;
    private String placeName;
    private Long imageId;
    private String reviewText;
    private LocalDateTime updatedAt;
}
