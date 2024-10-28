package com.ktb7.pinpung.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OAuth2LogoutCustomHandler implements LogoutHandler {

    @Value("${kakao.client_id}")
    private String clientId;

    private String logoutRedirectUri = "http://localhost:8080/logout";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.out.println("CustomLogoutHandler: Logging out...");

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Session invalidated.");
        }

        // 카카오 로그아웃 URL 생성
        String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout"
                + "?client_id=" + clientId
                + "&logout_redirect_uri=" + logoutRedirectUri;

        // 카카오 로그아웃 요청
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(kakaoLogoutUrl, String.class);

            if (resp.getStatusCode() == HttpStatus.FOUND) {
                // 로그아웃 성공 시에만 리다이렉트
                System.out.println("Successfully logged out from Kakao.");
                response.sendRedirect(logoutRedirectUri);
            } else {
                System.out.println("Failed to logout from Kakao. Status code: " + resp.getStatusCode());
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to logout from Kakao");
            }
        } catch (Exception ex) {
            System.out.println("An error occurred while logging out from Kakao. Error: " + ex.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
