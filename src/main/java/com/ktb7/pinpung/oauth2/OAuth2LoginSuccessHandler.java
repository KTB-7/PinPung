package com.ktb7.pinpung.oauth2;

import com.ktb7.pinpung.entity.Token;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.TokenRepository;
import com.ktb7.pinpung.repository.UserRepository;
import com.ktb7.pinpung.util.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        log.info("Authentication Success Triggered - SecurityContextHolder contains: {}", authentication);

        try {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

                if (authorizedClient == null) {
                    throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_CLIENT, "클라이언트 인증 실패");
                }

                Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
                Long kakaoId = Long.parseLong(String.valueOf(attributes.get("id")));

                // 유효성 검증
                ValidationUtils.validateUserId(kakaoId);

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

//                    // expiresIn 값을 초 단위로 계산하여 저장
//                    Instant expiresAt = authorizedClient.getRefreshToken().getExpiresAt();
//                    if (expiresAt != null) {
//                        long expiresInSeconds = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
//                        token.setExpiresIn(expiresInSeconds);
//                    }

                    tokenRepository.save(token);
                    log.info("Refresh token saved for user ID: {}", user.getUserId());
                } else {
                    log.warn("Refresh token is null for user ID: {}", user.getUserId());
                    throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER, "리프레시 토큰이 누락되었습니다.");
                }

                // 로그
                log.info("Access token and user info saved for user ID: {}", user.getUserId());

                // 리다이렉트 URL 구성
                String redirectUrl = String.format(
                        "http://localhost:3000/oauth/callback?status=success&token=%s&userId=%s&userName=%s&userEmail=%s",
                        URLEncoder.encode(authorizedClient.getAccessToken().getTokenValue(), StandardCharsets.UTF_8),
                        user.getUserId(),
                        URLEncoder.encode(user.getUserName(), StandardCharsets.UTF_8),
                        URLEncoder.encode(user.getUserEmail(), StandardCharsets.UTF_8)
                );

                // 리다이렉트
                log.info("Redirecting to: {}", redirectUrl);
                response.sendRedirect(redirectUrl);
            }
        } catch (CustomException e) {
            log.error("CustomException occurred during authentication success handling: {}", e.getMessage(), e);
            response.sendError(e.getStatus().value(), e.getErrorCode().getMsg());
        } catch (Exception e) {
            log.error("Unexpected error during authentication success handling: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMsg());
        }
    }
}