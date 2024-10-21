package com.ktb7.pinpung.exception.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // 잘못된 경로 변수로 인한 타입 변환 실패 처리 (400 Bad Request)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorDto> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        CustomException customException = new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMsg());
        return ErrorDto.toResponseEntity(customException);
    }

    // NoHandlerFoundException 처리 (404 Not Found)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode("404_NOT_FOUND")
                .msg("잘못된 요청입니다. 경로를 확인하세요.")
//                .detail("요청한 URL을 찾을 수 없습니다: " + ex.getRequestURL())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    // 추가적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("500_INTERNAL_SERVER_ERROR")
                .msg("서버 내부 오류가 발생했습니다.")
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}