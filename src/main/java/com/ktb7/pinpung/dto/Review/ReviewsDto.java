package com.ktb7.pinpung.dto.Review;

import com.ktb7.pinpung.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewsDto {
    private Integer count;
    private List<Review> reviews;

}
