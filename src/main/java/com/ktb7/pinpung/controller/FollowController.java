package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.FollowRequestDto;
import com.ktb7.pinpung.dto.FollowResponseDto;
import com.ktb7.pinpung.service.FollowService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/add")
    public ResponseEntity<FollowResponseDto> followUser(@RequestBody FollowRequestDto followRequestDto) {
        log.info("{}", followRequestDto);
        // 유효성 검증: id 검증
        ValidationUtils.validateUserId(followRequestDto.getUserId());
        ValidationUtils.validateUserRequest(followRequestDto.getUserId());
        ValidationUtils.validateUserId(followRequestDto.getWantsToFollowId());

        FollowResponseDto response = followService.followUser(followRequestDto.getUserId(), followRequestDto.getWantsToFollowId());
        return ResponseEntity.ok(response);
    }
}
