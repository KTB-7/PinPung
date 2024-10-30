package com.ktb7.pinpung.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LogoutCustomHandler implements LogoutHandler {

    @Value("${kakao.client_id}")
    private String clientId;

    private String logoutRedirectUri = "http://localhost:8080/logout-success";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.out.println("CustomLogoutHandler: Logging out...");

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Session invalidated.");
        }

        String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout"
                + "?client_id=" + clientId
                + "&logout_redirect_uri=" + logoutRedirectUri;

        try {
            // 카카오 로그아웃 URL로 리다이렉트
            response.sendRedirect(kakaoLogoutUrl);
        } catch (Exception ex) {
            System.out.println("An error occurred while redirecting to Kakao logout. Error: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
