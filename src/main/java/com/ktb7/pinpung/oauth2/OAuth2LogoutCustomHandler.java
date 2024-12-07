package com.ktb7.pinpung.oauth2;

import com.ktb7.pinpung.exception.common.LogoutFailureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LogoutCustomHandler implements LogoutHandler {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${LOGOUT_REDIRECT_URI}")
    private String logoutRedirectUri;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout"
                + "?client_id=" + clientId
                + "&logout_redirect_uri=" + logoutRedirectUri;

        try {
            response.sendRedirect(kakaoLogoutUrl);
        } catch (Exception ex) {
            throw new LogoutFailureException();
        }
    }
}
