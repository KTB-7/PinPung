package com.ktb7.pinpung.exception.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;
    private final String detail;

    public CustomException(HttpStatus status, ErrorCode errorCode, String detail) {
        this.status = status;
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
