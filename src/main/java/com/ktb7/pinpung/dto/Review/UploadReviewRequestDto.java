package com.ktb7.pinpung.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadReviewRequestDto {
    private Long placeId;
    private MultipartFile reviewImage;
    private String text;
}