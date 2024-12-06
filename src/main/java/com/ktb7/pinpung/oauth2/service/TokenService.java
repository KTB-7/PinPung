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
import jakarta.servlet.http.HttpServletRequest;
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

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUrl;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUrl;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> validateToken(String authorizationHeader) {
        // 헤더에서 토큰 가져오기
        String token = extractBearerToken(authorizationHeader);

        Long userId = getUserFromToken(token);

        return refreshToken(userId);
    }

    private ResponseEntity<?> refreshToken(Long userId) {
        ValidationUtils.validateUserId(userId);

        Token token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(kakaoTokenUrl)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "refresh_token")
                        .queryParam("client_id", clientId)
                        .queryParam("refresh_token", token.getRefreshToken())
                        .build())
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        if (kakaoTokenResponseDto != null && kakaoTokenResponseDto.getAccessToken() != null) {
            String newAccessToken = kakaoTokenResponseDto.getAccessToken();

            // 새로운 refreshToken이 응답에 포함된 경우 갱신
            if (kakaoTokenResponseDto.getRefreshToken() != null) {
                token.setRefreshToken(kakaoTokenResponseDto.getRefreshToken());
//                token.setExpiresIn(kakaoTokenResponseDto.getExpiresIn());
                tokenRepository.save(token);
            }

            TokenResponseDto tokenResponseDto = new TokenResponseDto(userId, newAccessToken);
            return ResponseEntity.ok(tokenResponseDto);
        } else {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_REFRESH_FAILED);
        }
    }

    public String extractBearerToken(String authorizationHeader) {
        // Authorization 헤더에서 Bearer 뒤의 토큰 추출
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 값만 가져옴
        } else {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN_OR_SOCIAL_ID);
        }

        // Bearer 뒤의 실제 토큰 값 반환
        return token;
    }

    public Long getUserFromToken(String token) {
        // 카카오 API를 통한 토큰 유효성 검증
        KakaoTokenInfoResponseDto kakaoTokenInfoResponseDto = WebClient.create(kakaoUserInfoUrl).get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(KakaoTokenInfoResponseDto.class)
                .block();

        Long userId = null;
        if (kakaoTokenInfoResponseDto == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN_OR_SOCIAL_ID);
        } else {
            User user = userRepository.findBySocialId(kakaoTokenInfoResponseDto.getId()).orElse(null);
            if (user == null) { throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND); }
            else userId = user.getUserId();
        }

        return userId;
    }
}
