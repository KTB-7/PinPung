package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.TokenResponseDto;
import com.ktb7.pinpung.service.TokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam Long userId, @RequestParam String expiredAccessToken) {
        ResponseEntity<?> response = tokenService.validateToken(userId, expiredAccessToken);

        return response;
    }
}
