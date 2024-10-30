package com.ktb7.pinpung.exception.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDto> handleCustomException(CustomException ex) {
        log.error("CustomException 발생: 상태 코드={}, 에러 메시지={}", ex.getStatus(), ex.getMessage());
        return ErrorDto.toResponseEntity(ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorDto> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException 발생: {}", ex.getMessage());
        return ErrorDto.toResponseEntity(new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST));
    }

    // 존재하지 않는 URL로 요청이 들어왔을 때 발생하는 예외 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.error("NoHandlerFoundException 발생: {}", ex.getMessage());
        return ErrorDto.toResponseEntity(new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        log.error("Exception 발생: {}", ex.getMessage(), ex);
        return ErrorDto.toResponseEntity(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR));
    }

    // OAuth2 인증 관련 예외 처리
    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleOAuth2AuthenticationException(OAuth2AuthenticationException ex) {
        log.error("OAuth2AuthenticationException 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto(HttpStatus.UNAUTHORIZED.value(), ErrorCode.AUTHENTICATION_FAILED.getCode(), "OAuth2 인증에 실패했습니다."));
    }
}
