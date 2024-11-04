package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.FollowRequestDto;
import com.ktb7.pinpung.dto.FollowResponseDto;
import com.ktb7.pinpung.dto.FollowReponseDto;
import com.ktb7.pinpung.dto.UnfollowResponseDto;
import com.ktb7.pinpung.service.FollowService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/add")
    public ResponseEntity<FollowResponseDto> followUser(@RequestBody FollowRequestDto followRequestDto) {
        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(followRequestDto.getUserId());
//        validationUtils.validateUserRequest(followRequestDto.getUserId());
        ValidationUtils.validateUserId(followRequestDto.getWantsToFollowId());

        FollowResponseDto response = followService.followUser(followRequestDto.getUserId(), followRequestDto.getWantsToFollowId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UnfollowResponseDto> unfollowUser(
            @RequestParam Long userId, @RequestParam Long wantsToUnfollowId) {
        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateUserId(wantsToUnfollowId);

        UnfollowResponseDto response = followService.unfollowUser(userId, wantsToUnfollowId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/followers", produces = "application/json")
    public ResponseEntity<FollowReponseDto> getFollowers(@RequestParam Long userId) {
        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);

        FollowReponseDto response = followService.getFollowers(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/followings", produces = "application/json")
    public ResponseEntity<FollowReponseDto> getFollowings(@RequestParam Long userId) {
        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(userId);

        FollowReponseDto response = followService.getFollowings(userId);

        return ResponseEntity.ok(response);
    }
}
