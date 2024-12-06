package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Follow.*;
import com.ktb7.pinpung.oauth2.service.TokenService;
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
    private final TokenService tokenService;

    @PostMapping
    @Operation(
            summary = "팔로우 요청",
            description = "특정 사용자를 팔로우합니다.",
            parameters = @Parameter(name = "followRequestDto", description = "팔로우 요청 정보 (userId, wantsToFollowId)", required = true)
    )
    public ResponseEntity<FollowResponseDto> followUser(@RequestHeader("Authorization") String authorizationHeader, @RequestBody FollowRequestDto followRequestDto) {
        log.info("Received request to /follows/add with followRequestDto: {}", followRequestDto.getWantsToFollowId());

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateUserId(followRequestDto.getWantsToFollowId());

        FollowResponseDto response = followService.followUser(userId, followRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(
            summary = "언팔로우 요청",
            description = "특정 사용자를 언팔로우합니다.",
            parameters = @Parameter(name = "unfollowRequestDto", description = "언팔로우 요청 정보 (userId, wantsToUnfollowId)", required = true)
    )
    public ResponseEntity<UnfollowResponseDto> unfollowUser(@RequestHeader("Authorization") String authorizationHeader, @RequestBody UnfollowRequestDto unfollowRequestDto) {
        log.info("Received request to /follows/add with: {}", unfollowRequestDto.getWantsToUnfollowId());

        String token = tokenService.extractBearerToken(authorizationHeader);
        Long userId = tokenService.getUserFromToken(token);

        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateUserId(unfollowRequestDto.getWantsToUnfollowId());

        UnfollowResponseDto response = followService.unfollowUser(userId, unfollowRequestDto);
        return ResponseEntity.ok(response);
    }
}
