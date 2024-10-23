package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.*;
import com.ktb7.pinpung.entity.Token;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.repository.TokenRepository;
import com.ktb7.pinpung.repository.UserRepository;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId, UserRepository userRepository, TokenRepository tokenRepository) {
        this.clientId = clientId;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    public LoginResponseDto getAccessToken(String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();  // 동기적으로 값을 가져오려면 block() 사용

//        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
//        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());

        KakaoUserInfoResponseDto userInfo = getUserInfo(kakaoTokenResponseDto.getAccessToken());
        LoginResponseDto loginResponseDto = kakaoLogin(userInfo);

        String accessToken = kakaoTokenResponseDto.getAccessToken();
        Integer expiresIn = kakaoTokenResponseDto.getExpiresIn();
        loginResponseDto.setExpiresIn(expiresIn);
        loginResponseDto.setAccessToken(accessToken);

        storeToken(kakaoTokenResponseDto, userInfo.getId());
        return loginResponseDto;
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST).get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

//        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
//        log.info("[ Kakao Service ] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
//        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }

    private void storeToken(KakaoTokenResponseDto kakaoTokenResponseDto, Long userId) {
        // 리프레시 토큰 저장
        Token token = new Token();
        token.setUserId(userId);
        token.setRefreshToken(kakaoTokenResponseDto.getRefreshToken());
        token.setExpiresIn(kakaoTokenResponseDto.getRefreshTokenExpiresIn());
        tokenRepository.save(token);
    }


    public LoginResponseDto kakaoLogin(KakaoUserInfoResponseDto userInfo) {
        Long socialId = userInfo.getId();
        User user = userRepository.findBySocialId(socialId).orElse(null);  // 소셜 ID로 사용자 확인

        LoginResponseDto loginResponse = new LoginResponseDto();

        if (user == null) {
            // 회원가입 처리
            user = new User();
            user.setSocialId(socialId);
            user.setUserEmail(userInfo.getKakaoAccount().getEmail());
            user.setUserName(userInfo.getKakaoAccount().getProfile().getNickName());
            // S3에 이미지 저장
//            user.setProfileImageId(userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

            // 새 유저 저장
            userRepository.save(user);

            loginResponse.setLoginSuccess(true);  // 새 회원가입 성공 시 로그인으로 간주
            loginResponse.setUser(user);  // 유저 정보 반환
        } else {
            // 기존 사용자 로그인 처리
            loginResponse.setLoginSuccess(true);  // 로그인 성공
            loginResponse.setUser(user);  // 유저 정보 반환
        }

        return loginResponse;
    }
}