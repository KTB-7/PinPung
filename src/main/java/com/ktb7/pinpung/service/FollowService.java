package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.FollowResponseDto;
import com.ktb7.pinpung.entity.Follow;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.FollowRepository;
import com.ktb7.pinpung.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public FollowResponseDto followUser(Long userId, Long wantsToFollowId) {
        // db에 두 아이디가 존재하는지 확인하고 유저 불러오기
        User user = userRepository.findByUserId(userId).orElseThrow(() ->
                new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));

        User wantsToFollow = userRepository.findByUserId(wantsToFollowId).orElseThrow(() ->
                new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));

        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowing(wantsToFollow);

        try {
            // Follow 엔티티 저장
            followRepository.save(follow);
            log.info("User {} followed User {}", userId, wantsToFollowId);
        } catch (Exception e) {
            log.error("Error saving follow relationship between user {} and user {}", userId, wantsToFollowId, e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR);
        }

        // FollowResponseDto 반환
        return new FollowResponseDto(userId, wantsToFollowId);
    }
}
