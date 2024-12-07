package com.ktb7.pinpung.exception.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorDto {

    private int status;  // HttpStatus의 상태 코드
    private String errorCode;  // ErrorCode의 코드
    private String msg;  // ErrorCode의 메시지

    public static ResponseEntity<ErrorDto> toResponseEntity(CustomException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorDto.builder()
                        .status(ex.getStatus().value())
                        .errorCode(ex.getErrorCode().getCode())
                        .msg(ex.getErrorCode().getMsg())
                        .build());
    }
}
