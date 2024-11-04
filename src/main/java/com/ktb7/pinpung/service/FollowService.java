package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.FollowResponseDto;
import com.ktb7.pinpung.dto.FollowReponseDto;
import com.ktb7.pinpung.dto.SimpleUserDto;
import com.ktb7.pinpung.dto.UnfollowResponseDto;
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

import java.util.List;
import java.util.stream.Collectors;

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

    public UnfollowResponseDto unfollowUser(Long userId, Long wantsToUnfollowId) {
        // 두 아이디가 존재하는지 확인하고 유저 불러오기
        User user = userRepository.findByUserId(userId).orElseThrow(() ->
                new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));

        User wantsToUnfollow = userRepository.findByUserId(wantsToUnfollowId).orElseThrow(() ->
                new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));

        // Follow 관계가 존재하는지 확인
        Follow follow = followRepository.findByFollowerAndFollowing(user, wantsToUnfollow).orElseThrow(() ->
                new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FOLLOW_RELATION_NOT_FOUND));

        try {
            // Follow 엔티티 삭제
            followRepository.delete(follow);
            log.info("User {} unfollowed User {}", userId, wantsToUnfollowId);
        } catch (Exception e) {
            log.error("Error deleting follow relationship between user {} and user {}", userId, wantsToUnfollowId, e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR);
        }

        // UnfollowResponseDto 반환
        return new UnfollowResponseDto(userId, wantsToUnfollowId);
    }

    public FollowReponseDto getFollowers(Long userId) {
        // 사용자가 존재하는지 확인
        userRepository.findByUserId(userId).orElseThrow(() ->
                new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));

        List<User> followers = followRepository.findFollowersByUserId(userId);
        List<SimpleUserDto> followerList = followers.stream()
                .map(follower -> new SimpleUserDto(follower.getUserId(), follower.getUserName()))
                .collect(Collectors.toList());

        return new FollowReponseDto(followerList.size(), followerList);
    }

    public FollowReponseDto getFollowings(Long userId) {
        // 사용자가 존재하는지 확인
        userRepository.findByUserId(userId).orElseThrow(() ->
                new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));

        List<User> followings = followRepository.findFollowingsByUserId(userId);
        List<SimpleUserDto> followingList = followings.stream()
                .map(follower -> new SimpleUserDto(follower.getUserId(), follower.getUserName()))
                .collect(Collectors.toList());

        return new FollowReponseDto(followingList.size(), followingList);
    }
}
