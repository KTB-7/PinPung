package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Follow.FollowsResponseDto;
import com.ktb7.pinpung.dto.Profile.ProfileWithPungResponseDto;
import com.ktb7.pinpung.dto.Profile.ProfileWithReviewResponseDto;
import com.ktb7.pinpung.dto.Review.MessageResponseDto;
import com.ktb7.pinpung.dto.User.TasteRequestDto;
import com.ktb7.pinpung.service.FollowService;
import com.ktb7.pinpung.service.UserService;
import com.ktb7.pinpung.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{userId}")
@Tag(name = "User API", description = "유저/마이페이지 관련 API")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @GetMapping
    @Operation(
            summary = "마이페이지 조회",
            description = "주어진 유저 ID에 대한 팔로잉, 팔로워, 펑을 전부 조회합니다",
            parameters = {
                    @Parameter(name = "userId", description = "조회할 유저의 ID", required = true, example = "123"),
            }
    )
    public ResponseEntity<ProfileWithPungResponseDto> myProfile(@PathVariable Long userId) {
        // 유효성 검증
        ValidationUtils.validateUserId(userId);

        ProfileWithPungResponseDto profile = userService.viewProfileWithPung(userId);

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/view-reviews")
    @Operation(
            summary = "마이페이지 조회(리뷰 기준)",
            description = "주어진 유저 ID에 대한 팔로잉, 팔로워, 리뷰를 전부 조회합니다",
            parameters = {
                    @Parameter(name = "userId", description = "조회할 유저의 ID", required = true, example = "123"),
            }
    )
    public ResponseEntity<ProfileWithReviewResponseDto> myProfileWithReview(@PathVariable Long userId) {
        // 유효성 검증
        ValidationUtils.validateUserId(userId);

        ProfileWithReviewResponseDto profile = userService.viewProfileWithReview(userId);

        return ResponseEntity.ok(profile);
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


    @PostMapping("/taste")
    public void getTaste(@RequestBody TasteRequestDto tasteRequestDto) {
        log.info("Received request to /taste");

        Integer age = tasteRequestDto.getAge();
        List<String> activities = tasteRequestDto.getActivities();
        List<String> menus = tasteRequestDto.getMenus();

        // 유효성 검증
        ValidationUtils.validateAge(age);
        ValidationUtils.validateListString(activities);
        ValidationUtils.validateListString(menus);

        userService.setTaste(tasteRequestDto);


    }
}
