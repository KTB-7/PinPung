package com.ktb7.pinpung.exception.common;

import org.springframework.http.HttpStatus;

public class LogoutFailureException extends CustomException {

    public LogoutFailureException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.LOGOUT_FAILED, ErrorCode.LOGOUT_FAILED.getMsg());
    }
}
