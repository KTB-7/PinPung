//package com.ktb7.pinpung.service;
//
//import com.ktb7.pinpung.dto.*;
//import com.ktb7.pinpung.entity.Token;
//import com.ktb7.pinpung.entity.User;
//import com.ktb7.pinpung.repository.TokenRepository;
//import com.ktb7.pinpung.repository.UserRepository;
//import io.netty.handler.codec.http.HttpHeaderValues;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class KakaoService {
//
//    @Value("${kakao.client_id}")
//    private String clientId;
//
//    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
//    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
//    private final UserRepository userRepository;
//    private final TokenRepository tokenRepository;
//
//    public LoginResponseDto getAccessTokenAndSave(String code) {
//        // 카카오에서 토큰 발급
//        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/oauth/token")
//                        .queryParam("grant_type", "authorization_code")
//                        .queryParam("client_id", clientId)
//                        .queryParam("code", code)
//                        .build(true))
//                .retrieve()
//                .bodyToMono(KakaoTokenResponseDto.class)
//                .block();
//
//        // 유저 정보 가져오기
//        KakaoUserInfoResponseDto userInfo = getUserInfo(kakaoTokenResponseDto.getAccessToken());
//        LoginResponseDto loginResponseDto = kakaoLogin(userInfo);
//
//        // 액세스 토큰과 리프레시 토큰 저장
//        storeToken(kakaoTokenResponseDto, userInfo.getId());
//
//        return loginResponseDto;
//    }
//
//    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
//        return WebClient.create(KAUTH_USER_URL_HOST).get()
//                .uri(uriBuilder -> uriBuilder.path("/v2/user/me").build(true))
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .retrieve()
//                .bodyToMono(KakaoUserInfoResponseDto.class)
//                .block();
//    }
//
//    private void storeToken(KakaoTokenResponseDto kakaoTokenResponseDto, Long userId) {
//        Token token = tokenRepository.findByUserId(userId).orElse(new Token());
//        token.setUserId(userId);
//        token.setAccessToken(kakaoTokenResponseDto.getAccessToken());
//        token.setRefreshToken(kakaoTokenResponseDto.getRefreshToken());
//        token.setExpiresIn(kakaoTokenResponseDto.getExpiresIn());
//        tokenRepository.save(token);
//    }
//
//    public KakaoTokenResponseDto refreshAccessToken(String refreshToken) {
//        // 리프레시 토큰을 이용해 새로운 액세스 토큰 발급
//        return WebClient.create(KAUTH_TOKEN_URL_HOST).post()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/oauth/token")
//                        .queryParam("grant_type", "refresh_token")
//                        .queryParam("client_id", clientId)
//                        .queryParam("refresh_token", refreshToken)
//                        .build(true))
//                .retrieve()
//                .bodyToMono(KakaoTokenResponseDto.class)
//                .block();
//    }
//
//    public LoginResponseDto kakaoLogin(KakaoUserInfoResponseDto userInfo) {
//        Long socialId = userInfo.getId();
//        User user = userRepository.findBySocialId(socialId).orElse(null);  // 소셜 ID로 사용자 확인
//
//        LoginResponseDto loginResponse = new LoginResponseDto();
//
//        if (user == null) {
//            // 회원가입 처리
//            user = new User();
//            user.setSocialId(socialId);
//            user.setUserEmail(userInfo.getKakaoAccount().getEmail());
//            user.setUserName(userInfo.getKakaoAccount().getProfile().getNickName());
//            // 필요한 경우 프로필 이미지 등 추가 정보도 저장
//
//            // 새 유저 저장
//            userRepository.save(user);
//
//            loginResponse.setLoginSuccess(true);  // 새 회원가입 성공 시 로그인으로 간주
//            loginResponse.setUser(user);  // 유저 정보 반환
//        } else {
//            // 기존 사용자 로그인 처리
//            loginResponse.setLoginSuccess(true);  // 로그인 성공
//            loginResponse.setUser(user);  // 유저 정보 반환
//        }
//
//        return loginResponse;
//    }
//
//}
