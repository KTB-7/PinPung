package com.ktb7.pinpung.oauth2.service;

import com.ktb7.pinpung.oauth2.dto.KakaoTokenResponseDto;
import com.ktb7.pinpung.oauth2.dto.KakaoTokenInfoResponseDto;
import com.ktb7.pinpung.oauth2.dto.TokenResponseDto;
import com.ktb7.pinpung.entity.Token;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.repository.TokenRepository;
import com.ktb7.pinpung.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long socialId = user.getSocialId();

        KakaoTokenInfoResponseDto kakaoTokenInfoResponseDto = WebClient.create(KAUTH_USER_URL_HOST).get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/user/access_token_info")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(KakaoTokenInfoResponseDto.class)
                .block();


        // socialId와 토큰 정보의 ID 검증
        if (kakaoTokenInfoResponseDto == null || !socialId.equals(kakaoTokenInfoResponseDto.getId())) {
            return ResponseEntity.badRequest().body("Invalid token or social ID");
        }

        // 리프레시 토큰으로 새로운 액세스 토큰 발급 요청
        return refreshToken(userId);
    }

    // 리프레시 토큰을 사용해 액세스 토큰 갱신
    private ResponseEntity<?> refreshToken(Long userId) {
        // 해당 유저의 리프레시 토큰 가져오기
        Token token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User's refresh token not found"));

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

            // 새로 발급된 액세스 토큰을 TokenResponseDto에 담아서 반환
            TokenResponseDto tokenResponseDto = new TokenResponseDto(userId, newAccessToken);
            return ResponseEntity.ok(tokenResponseDto);
        } else {
            return ResponseEntity.status(500).body("Failed to refresh access token");
        }
    }
}