package com.ktb7.pinpung.exception.common;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;

    // ErrorCode와 HttpStatus를 받는 생성자
    public CustomException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.status = status;
        this.errorCode = errorCode;
    }

    // ErrorCode와 HttpStatus, 추가 메시지를 받는 생성자
    public CustomException(HttpStatus status, ErrorCode errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
