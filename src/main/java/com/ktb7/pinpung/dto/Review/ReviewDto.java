package com.ktb7.pinpung.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@AllArgsConstructor
@Getter
public class ReviewDto {

    private Long reviewId;
    private Long userId;
    private String userName;
    private Long imageId;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
