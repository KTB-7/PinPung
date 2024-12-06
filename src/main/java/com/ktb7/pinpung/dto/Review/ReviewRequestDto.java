package com.ktb7.pinpung.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private Long userId;
    private Long placeId;
    private String text;
    private MultipartFile image;
}
