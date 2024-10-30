package com.ktb7.pinpung.exception.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDto> handleCustomException(CustomException ex) {
        return ErrorDto.toResponseEntity(ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorDto> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ErrorDto.toResponseEntity(new CustomException(ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ErrorDto.toResponseEntity(new CustomException(ErrorCode.PLACE_NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        return ErrorDto.toResponseEntity(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    // OAuth2 인증 관련 예외 처리
    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleOAuth2AuthenticationException(OAuth2AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto(HttpStatus.UNAUTHORIZED.value(), ErrorCode.AUTHENTICATION_FAILED.getCode(), "OAuth2 인증에 실패했습니다."));
    }
}
