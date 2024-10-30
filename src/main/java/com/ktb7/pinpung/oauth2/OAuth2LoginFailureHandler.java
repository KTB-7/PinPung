package com.ktb7.pinpung.oauth2;

import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauthException = (OAuth2AuthenticationException) exception;
            String errorCode = oauthException.getError().getErrorCode();

            if ("unauthorized_client".equals(errorCode)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_CLIENT);
            } else if ("invalid_token".equals(errorCode)) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            } else if ("expired_token".equals(errorCode)) {
                throw new CustomException(ErrorCode.EXPIRED_TOKEN);
            } else {
                throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
            }
        } else {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }
}
