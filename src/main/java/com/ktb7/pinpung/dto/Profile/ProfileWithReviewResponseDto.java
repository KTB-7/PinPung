package com.ktb7.pinpung.dto.Profile;

import com.ktb7.pinpung.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ProfileWithReviewResponseDto {

    private DefaultProfileDto defaultProfile;
    private List<SimpleReview> reviews;
}
