package com.ktb7.pinpung.controller;

import com.ktb7.pinpung.dto.KakaoInfoResponseDto;
import com.ktb7.pinpung.dto.KakaoUserInfoResponseDto;
import com.ktb7.pinpung.dto.LoginResponseDto;
import com.ktb7.pinpung.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    // 프론트에서 받는 주소로 수정필요, /login/oauth2 ?
    // 인가 코드 받아서 토큰 발급받고 유저 정보 저장하기
    @GetMapping("/oauth")
    public ResponseEntity<LoginResponseDto> callback(@RequestParam("code") String code) throws IOException {
        String accessToken = kakaoService.getAccessToken(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
        // 로그인 또는 회원가입 처리
        LoginResponseDto loginResponse = kakaoService.kakaoLogin(userInfo);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);

    }
}
