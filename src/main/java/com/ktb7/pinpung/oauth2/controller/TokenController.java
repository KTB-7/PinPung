package com.ktb7.pinpung.oauth2.controller;

import com.ktb7.pinpung.oauth2.service.TokenService;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/token")
public class TokenController {

    private final TokenService tokenService;
    private final ValidationUtils validationUtils;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam Long userId, @RequestParam String expiredAccessToken) {
        // 유효성 검증: 현재 로그인된 사용자와 요청된 userId가 일치하는지 확인
//        validationUtils.validateUserRequest(userId);
        return tokenService.validateToken(userId, expiredAccessToken);
    }
}
