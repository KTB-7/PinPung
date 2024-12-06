package com.ktb7.pinpung.dto.Review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ModifyReviewRequestDto {
    private Long reviewId;
    private Long placeId;
    private MultipartFile reviewImage;
    private String text;
}
