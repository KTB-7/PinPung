package com.ktb7.pinpung.oauth2.controller;

import com.ktb7.pinpung.oauth2.service.TokenService;
import com.ktb7.pinpung.util.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/token")
public class TokenController {

    private final TokenService tokenService;
    private final ValidationUtils validationUtils;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {

        // HttpServletRequest를 TokenService로 전달
        return tokenService.validateToken(authorizationHeader);
    }

}
