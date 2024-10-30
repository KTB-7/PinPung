package com.ktb7.pinpung.exception.common;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;

    // 기존 생성자
    public CustomException(HttpStatus status, ErrorCode errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    // ErrorCode만 받는 생성자
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.status = HttpStatus.BAD_REQUEST;  // 기본값으로 설정
        this.errorCode = errorCode;
    }

    // ErrorCode와 HttpStatus를 받는 생성자
    public CustomException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.status = status;
        this.errorCode = errorCode;
    }

    // ErrorCode와 메시지를 받는 생성자 추가
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본값 설정
        this.errorCode = errorCode;
    }
}
