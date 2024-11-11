package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.*;
import com.ktb7.pinpung.service.FollowService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/add")
    public ResponseEntity<FollowResponseDto> followUser(@RequestBody FollowRequestDto followRequestDto) {
        log.info("Received request to /follows/add with followRequestDto: {} {}", followRequestDto.getUserId(), followRequestDto.getWantsToFollowId());

        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(followRequestDto.getUserId());
        ValidationUtils.validateUserId(followRequestDto.getWantsToFollowId());

        FollowResponseDto response = followService.followUser(followRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UnfollowResponseDto> unfollowUser(@RequestBody UnfollowRequestDto unfollowRequestDto) {
        log.info("Received request to /follows/add with: {} {}", unfollowRequestDto.getUserId(), unfollowRequestDto.getWantsToUnfollowId());

        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(unfollowRequestDto.getUserId());
        ValidationUtils.validateUserId(unfollowRequestDto.getWantsToUnfollowId());

        UnfollowResponseDto response = followService.unfollowUser(unfollowRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/followers", produces = "application/json")
    public ResponseEntity<FollowReponseDto> getFollowers(@RequestParam Long userId) {
        log.info("Received request to /followers with: {}", userId);

        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);

        FollowReponseDto response = followService.getFollowers(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/followings", produces = "application/json")
    public ResponseEntity<FollowReponseDto> getFollowings(@RequestParam Long userId) {
        log.info("Received request to /followings with: {}", userId);
        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);

        FollowReponseDto response = followService.getFollowings(userId);

        return ResponseEntity.ok(response);
    }
}
