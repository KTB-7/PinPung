package com.ktb7.pinpung.oauth2;

import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        try {
            if (exception instanceof OAuth2AuthenticationException) {
                OAuth2AuthenticationException oauthException = (OAuth2AuthenticationException) exception;
                String errorCode = oauthException.getError().getErrorCode();

                if ("unauthorized_client".equals(errorCode)) {
                    throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_CLIENT);
                } else if ("invalid_token".equals(errorCode)) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TOKEN_OR_SOCIAL_ID);
                } else if ("expired_token".equals(errorCode)) {
                    throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_TOKEN);
                } else {
                    throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.AUTHENTICATION_FAILED);
                }
            } else {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.AUTHENTICATION_FAILED);
            }
        } catch (CustomException e) {
            response.setStatus(e.getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.format(
                    "{\"status\": %d, \"errorCode\": \"%s\", \"msg\": \"%s\"}",
                    e.getStatus().value(),
                    e.getErrorCode().getCode(),
                    e.getErrorCode().getMsg()
            ));
        }
    }
}
