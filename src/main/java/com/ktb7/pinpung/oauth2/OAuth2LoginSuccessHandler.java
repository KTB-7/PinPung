package com.ktb7.pinpung.oauth2;

import com.ktb7.pinpung.entity.Token;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.repository.TokenRepository;
import com.ktb7.pinpung.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // SecurityContextHolder의 인증 정보 확인 로그
        log.info("Authentication Success Triggered - SecurityContextHolder contains: {}", SecurityContextHolder.getContext().getAuthentication());

        log.info("Checking if authorizedClientService is null: {}", authorizedClientService == null);
        try {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

                if (authorizedClient != null) {
                    // 사용자 정보 가져오기
                    Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
                    Long kakaoId = Long.parseLong(String.valueOf(attributes.get("id")));

                    // 사용자 저장 또는 업데이트
                    User user = userRepository.findBySocialId(kakaoId).orElseGet(() -> {
                        User newUser = new User();
                        newUser.setSocialId(kakaoId);
                        newUser.setUserEmail((String) ((Map<String, Object>) attributes.get("kakao_account")).get("email"));
                        newUser.setUserName((String) ((Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile")).get("nickname"));
                        userRepository.save(newUser);
                        log.info("New user created with Kakao ID: {}", kakaoId);
                        return newUser;
                    });

                    // 리프레시 토큰 저장
                    if (authorizedClient.getRefreshToken() != null) {
                        Token token = tokenRepository.findByUserId(user.getUserId()).orElse(new Token());
                        token.setUserId(user.getUserId());
                        token.setRefreshToken(authorizedClient.getRefreshToken().getTokenValue());
                        tokenRepository.save(token);
                        log.info("Refresh token saved for user ID: {}", user.getUserId());
                    } else {
                        log.warn("Refresh token is null for user ID: {}", user.getUserId());
                    }

                    // 액세스 토큰 및 유저 정보 JSON 응답 생성
                    String accessToken = authorizedClient.getAccessToken().getTokenValue();
                    String jsonResponse = String.format(
                            "{\"accessToken\":\"%s\", \"userId\": \"%s\", \"userName\": \"%s\", \"userEmail\": \"%s\"}",
                            accessToken, user.getUserId(), user.getUserName(), user.getUserEmail()
                    );

                    // JSON 응답 로그 출력
                    log.info("JSON Response: {}", jsonResponse);

                    // JSON 응답 작성
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(jsonResponse);
                } else {
                    log.warn("Authorized Client is null. Token information could not be loaded.");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증된 클라이언트를 찾을 수 없습니다.");
                }
            }
        } catch (Exception e) {
            log.error("Error during onAuthenticationSuccess: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 내부 오류 발생했습니다.");
        }
    }
}