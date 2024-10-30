//package com.ktb7.pinpung.oauth2;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.http.HttpStatus;
//
//import java.io.IOException;
//
//@Component
//public class OAuth2LogoutFailureHandler implements LogoutHandler {
//
//    @Override
//    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        // 로그아웃 실패 처리 로직
//        try {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json");
//            response.getWriter().write("{\"logout\": \"failed\", \"message\": \"로그아웃 실패\"}");
//            System.out.println("Logout failed.");
//        } catch (IOException e) {
//            System.out.println("An error occurred while writing the logout failure response: " + e.getMessage());
//        }
//    }
//}
