package com.ktb7.pinpung.util;

import com.ktb7.pinpung.entity.Follow;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.FollowRepository;
import com.ktb7.pinpung.repository.PlaceRepository;
import com.ktb7.pinpung.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositoryHelper {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PlaceRepository placeRepository;

    // User : ID로 조회
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
    }

    // Follow : Follower와 Following 관계를 조회
    public Follow findFollowRelation(User follower, User following) {
        return followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.FOLLOW_RELATION_NOT_FOUND));
    }

    // Place : placeId로
    public Place findPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND));
    }

}
