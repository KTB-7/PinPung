package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.Follow.*;
import com.ktb7.pinpung.entity.Follow;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.FollowRepository;
import com.ktb7.pinpung.repository.UserRepository;
import com.ktb7.pinpung.util.RepositoryHelper;
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
    private final RepositoryHelper repositoryHelper;

    public FollowResponseDto followUser(Long userId, FollowRequestDto followRequestDto) {
        Long wantsToFollowId = followRequestDto.getWantsToFollowId();

        // db에 두 아이디가 존재하는지 확인하고 유저 불러오기
        User user = repositoryHelper.findUserById(userId);
        User wantsToFollow = repositoryHelper.findUserById(wantsToFollowId);

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

    public UnfollowResponseDto unfollowUser(Long userId, UnfollowRequestDto unfollowRequestDto) {
        Long wantsToUnfollowId = unfollowRequestDto.getWantsToUnfollowId();

        // 두 아이디가 존재하는지 확인하고 유저 불러오기
        User user = repositoryHelper.findUserById(userId);
        User wantsToUnfollow = repositoryHelper.findUserById(wantsToUnfollowId);

        // Follow 관계가 존재하는지 확인
        Follow follow = repositoryHelper.findFollowRelation(user, wantsToUnfollow);

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

    public FollowsResponseDto getFollowers(Long userId) {
        // 사용자가 존재하는지 확인
        repositoryHelper.findUserById(userId);

        List<User> followers = followRepository.findFollowersByUserId(userId);
        List<SimpleUserDto> followerList = followers.stream()
                .map(follower -> new SimpleUserDto(follower.getUserId(), follower.getUserName()))
                .collect(Collectors.toList());

        return new FollowsResponseDto(followerList.size(), followerList);
    }

    public FollowsResponseDto getFollowings(Long userId) {
        // 사용자가 존재하는지 확인
        repositoryHelper.findUserById(userId);

        List<User> followings = followRepository.findFollowingsByUserId(userId);
        List<SimpleUserDto> followingList = followings.stream()
                .map(follower -> new SimpleUserDto(follower.getUserId(), follower.getUserName()))
                .collect(Collectors.toList());

        return new FollowsResponseDto(followingList.size(), followingList);
    }
}
