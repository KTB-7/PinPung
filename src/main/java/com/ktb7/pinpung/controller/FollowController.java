package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Follow.*;
import com.ktb7.pinpung.service.FollowService;
import com.ktb7.pinpung.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/follows")
@Tag(name = "Follow API", description = "팔로우/언팔로우 및 팔로워/팔로잉 조회 관련 API")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    @Operation(
            summary = "팔로우 요청",
            description = "특정 사용자를 팔로우합니다.",
            parameters = @Parameter(name = "followRequestDto", description = "팔로우 요청 정보 (userId, wantsToFollowId)", required = true)
    )
    public ResponseEntity<FollowResponseDto> followUser(@RequestBody FollowRequestDto followRequestDto) {
        log.info("Received request to /follows/add with followRequestDto: {} {}", followRequestDto.getUserId(), followRequestDto.getWantsToFollowId());

        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(followRequestDto.getUserId());
        ValidationUtils.validateUserId(followRequestDto.getWantsToFollowId());

        FollowResponseDto response = followService.followUser(followRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(
            summary = "언팔로우 요청",
            description = "특정 사용자를 언팔로우합니다.",
            parameters = @Parameter(name = "unfollowRequestDto", description = "언팔로우 요청 정보 (userId, wantsToUnfollowId)", required = true)
    )
    public ResponseEntity<UnfollowResponseDto> unfollowUser(@RequestBody UnfollowRequestDto unfollowRequestDto) {
        log.info("Received request to /follows/add with: {} {}", unfollowRequestDto.getUserId(), unfollowRequestDto.getWantsToUnfollowId());

        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(unfollowRequestDto.getUserId());
        ValidationUtils.validateUserId(unfollowRequestDto.getWantsToUnfollowId());

        UnfollowResponseDto response = followService.unfollowUser(unfollowRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/followers", produces = "application/json")
    @Operation(
            summary = "팔로워 목록 조회",
            description = "특정 사용자의 팔로워 목록을 조회합니다.",
            parameters = @Parameter(name = "userId", description = "조회할 사용자 ID", required = true, example = "123")
    )
    public ResponseEntity<FollowsResponseDto> getFollowers(@RequestParam Long userId) {
        log.info("Received request to /followers with: {}", userId);

        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);

        FollowsResponseDto response = followService.getFollowers(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/followings", produces = "application/json")
    @Operation(
            summary = "팔로잉 목록 조회",
            description = "특정 사용자가 팔로잉한 사용자의 목록을 조회합니다.",
            parameters = @Parameter(name = "userId", description = "조회할 사용자 ID", required = true, example = "123")
    )
    public ResponseEntity<FollowsResponseDto> getFollowings(@RequestParam Long userId) {
        log.info("Received request to /followings with: {}", userId);
        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);

        FollowsResponseDto response = followService.getFollowings(userId);

        return ResponseEntity.ok(response);
    }
}
