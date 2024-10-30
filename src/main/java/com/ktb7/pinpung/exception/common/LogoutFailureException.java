package com.ktb7.pinpung.exception.common;

import org.springframework.http.HttpStatus;

public class LogoutFailureException extends CustomException {

    public LogoutFailureException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, "로그아웃 중 오류가 발생했습니다.");
    }

    public LogoutFailureException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, message);
    }
}
