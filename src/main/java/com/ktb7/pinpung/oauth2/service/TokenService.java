package com.ktb7.pinpung.oauth2.service;

import com.ktb7.pinpung.entity.Token;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.oauth2.dto.KakaoTokenInfoResponseDto;
import com.ktb7.pinpung.oauth2.dto.KakaoTokenResponseDto;
import com.ktb7.pinpung.oauth2.dto.TokenResponseDto;
import com.ktb7.pinpung.repository.TokenRepository;
import com.ktb7.pinpung.repository.UserRepository;
import com.ktb7.pinpung.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${kakao.client_id}")
    private String clientId;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    public ResponseEntity<?> validateToken(Long userId, String token) {
        // 유효성 검증
        ValidationUtils.validateUserId(userId);
        ValidationUtils.validateAccessToken(token);

        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        Long socialId = user.getSocialId();

        // 카카오 API를 통한 토큰 유효성 검증
        KakaoTokenInfoResponseDto kakaoTokenInfoResponseDto = WebClient.create(KAUTH_USER_URL_HOST).get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/user/access_token_info")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(KakaoTokenInfoResponseDto.class)
                .block();

        if (kakaoTokenInfoResponseDto == null || !socialId.equals(kakaoTokenInfoResponseDto.getId())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN_OR_SOCIAL_ID);
        }

        return refreshToken(userId);
    }

    private ResponseEntity<?> refreshToken(Long userId) {
        ValidationUtils.validateUserId(userId);

        Token token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "refresh_token")
                        .queryParam("client_id", clientId)
                        .queryParam("refresh_token", token.getRefreshToken())
                        .build())
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        if (kakaoTokenResponseDto != null && kakaoTokenResponseDto.getAccessToken() != null) {
            String newAccessToken = kakaoTokenResponseDto.getAccessToken();
            TokenResponseDto tokenResponseDto = new TokenResponseDto(userId, newAccessToken);
            return ResponseEntity.ok(tokenResponseDto);
        } else {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_REFRESH_FAILED);
        }
    }
}
