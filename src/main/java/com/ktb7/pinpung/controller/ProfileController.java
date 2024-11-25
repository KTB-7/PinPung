package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.Profile.ProfileWithPungResponseDto;
import com.ktb7.pinpung.dto.Profile.ProfileWithReviewResponseDto;
import com.ktb7.pinpung.service.ProfileService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{userName}")
    public ResponseEntity<ProfileWithPungResponseDto> myProfile(@PathVariable String userName) {
        // 유효성 검증
        ValidationUtils.validateUserName(userName);

        ProfileWithPungResponseDto profile = profileService.viewProfileWithPung(userName);

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{userName}/view-reviews")
    public ResponseEntity<ProfileWithReviewResponseDto> myProfileWithReview(@PathVariable String userName) {
        // 유효성 검증
        ValidationUtils.validateUserName(userName);

        ProfileWithReviewResponseDto profile = profileService.viewProfileWithReview(userName);

        return ResponseEntity.ok(profile);
    }

}
