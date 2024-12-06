package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.Profile.*;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final Clock clock;
    private final PungRepository pungRepository;
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;

    public ProfileWithPungResponseDto viewProfileWithPung(Long userId) {

        DefaultProfileDto defaultProfileDto = getDefaultProfile(userId);

        List<SimplePung> simplePungs = pungRepository.findSimplePungByUserId(userId);

        return new ProfileWithPungResponseDto(defaultProfileDto, simplePungs);

    }

    public ProfileWithReviewResponseDto viewProfileWithReview(Long userId) {

        DefaultProfileDto defaultProfileDto = getDefaultProfile(userId);

        List<Review> reviews = reviewRepository.findByUserId(userId);

        List<Long> placeIds = reviews.stream()
                .map(Review::getPlaceId)
                .distinct()
                .toList();

        Map<Long, String> placeIdToNameMap = placeRepository.findPlaceNamesByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (String) result[1]
                ));

        List<SimpleReview> simpleReviews = reviews.stream()
                .map(review -> new SimpleReview(
                        review.getReviewId(),
                        userId,
                        review.getPlaceId(),
                        placeIdToNameMap.get(review.getPlaceId()),
                        review.getImageId(),
                        review.getText(),
                        review.getUpdatedAt()
                ))
                .toList();

        return new ProfileWithReviewResponseDto(defaultProfileDto, simpleReviews);

    }

    public DefaultProfileDto getDefaultProfile(Long userId) {
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        // 팔로워 및 팔로잉 정보 조회
        List<User> followers = followRepository.findFollowersByUserId(userId);
        List<User> followings = followRepository.findFollowingsByUserId(userId);

        Long reviewCount = reviewRepository.countByUserId(userId);
        Long pungCount = pungRepository.countByUserId(userId);

        // DefaultProfileDto 반환
        return new DefaultProfileDto(
                user.getUserId(),
                user.getUserName(),
                followers.size(),
                followings.size(),
                pungCount,
                reviewCount
        );
    }
}
