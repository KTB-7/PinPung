//package com.ktb7.pinpung.config;
//
//import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
//import javax.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class TokenLoggingFilter extends AbstractPreAuthenticatedProcessingFilter {
//
//    @Override
//    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
//        String token = request.getHeader("Authorization");
//        if (token != null) {
//            log.debug("Authorization Token: {}", token); // 토큰 로그 출력
//        } else {
//            log.debug("Authorization Token not found in request");
//        }
//        return null;
//    }
//
//    @Override
//    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
//        return "N/A";
//    }
//}
//
