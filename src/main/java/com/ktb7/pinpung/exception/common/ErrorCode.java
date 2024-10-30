package com.ktb7.pinpung.exception.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    UNKNOWN("000_UNKNOWN", "알 수 없는 에러가 발생했습니다."),
    BAD_REQUEST("400_BAD_REQUEST", "잘못된 요청입니다."),
    VALIDATION_FAILED("400_VALIDATION_FAILED", "유효성 검증에 실패했습니다."),
    MISSING_PARAMETER("400_MISSING_PARAMETER", "필수 파라미터가 누락되었습니다."),
    INVALID_PARAMETER("400_INVALID_PARAMETER", "잘못된 파라미터가 포함되었습니다."),

    // 로그인 관련 오류
    AUTHENTICATION_FAILED("400_AUTHENTICATION_FAILED", "인증에 실패했습니다."),
    UNAUTHORIZED_CLIENT("400_UNAUTHORIZED_CLIENT", "권한이 부족합니다."),
    INVALID_TOKEN("400_INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("400_EXPIRED_TOKEN", "토큰이 만료되었습니다."),

    // 404 Not Found
    PLACE_NOT_FOUND("404_PLACE_NOT_FOUND", "등록되지 않은 장소입니다."),
    TAG_NOT_FOUND("404_TAG_NOT_FOUND", "해당 태그를 찾을 수 없습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("500_INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR("500_DATABASE_ERROR", "데이터베이스 처리 중 오류가 발생했습니다."),
    FILE_UPLOAD_FAILED("500_FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),
    UNEXPECTED_ERROR("500_UNEXPECTED_ERROR", "예상치 못한 오류가 발생했습니다.");

    private final String code;
    private final String msg;
}
