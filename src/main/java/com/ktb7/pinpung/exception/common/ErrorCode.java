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
    EXPIRED_TOKEN("400_EXPIRED_TOKEN", "토큰이 만료되었습니다."),
    LOGOUT_FAILED("500_LOGOUT_FAILED", "로그아웃 중 오류가 발생했습니다."),
    USER_NOT_FOUND("404_USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND("404_REFRESH_TOKEN_NOT_FOUND", "리프레시 토큰을 찾을 수 없습니다."),
    INVALID_TOKEN_OR_SOCIAL_ID("400_INVALID_TOKEN", "유효하지 않은 토큰 또는 소셜 ID 불일치"),
    TOKEN_REFRESH_FAILED("500_TOKEN_REFRESH_FAILED", "액세스 토큰 갱신에 실패했습니다."),

    // 404 Not Found
    PLACE_NOT_FOUND("404_PLACE_NOT_FOUND", "등록되지 않은 장소입니다."),
    REVIEW_NOT_FOUND("404_REVIEW_NOT_FOUND", "등록되지 않은 리뷰 정보입니다."),
    TAG_NOT_FOUND("404_TAG_NOT_FOUND", "해당 태그를 찾을 수 없습니다."),
    FOLLOW_RELATION_NOT_FOUND("404_FOLLOW_RELATION_NOT_FOUND", "팔로우 관계를 찾을 수 없습니다."),
    KAKAO_API_CALL_FAILED("500_KAKAO_API_CALL_FAILED", "카카오 API 호출에 실패했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("500_INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR("500_DATABASE_ERROR", "데이터베이스 처리 중 오류가 발생했습니다."),
    IMAGE_UPLOAD_FAILED("500_IMAGE_UPLOAD_FAILED", "이미지 업로드에 실패했습니다."),
//    IMAGE_DOWNLOAD_FAILED("500_IMAGE_DOWNLOAD_FAILED", "이미지 다운로드 중 오류가 발생했습니다."),
    FILE_PROCESSING_FAILED("500_FILE_PROCESSING_FAILED", "파일 처리 중 오류가 발생했습니다."),
    FILE_UPLOAD_FAILED("500_FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),
    UNEXPECTED_ERROR("500_UNEXPECTED_ERROR", "예상치 못한 오류가 발생했습니다."),
    API_CALL_FAILED("500_API_CALL_FAILED", "API CALL에 실패했습니다."),

    RECOMMEND_TAGS_REQUEST_FAILED("500_RECOMMEND_TAGS_REQUEST_FAILED", "추천 태그 요청 중 오류가 발생했습니다."),
    RECOMMEND_TAGS_RESPONSE_INVALID("500_RECOMMEND_TAGS_RESPONSE_INVALID", "추천 태그 API 응답이 유효하지 않습니다."),
    RECOMMEND_TAGS_DATA_PROCESSING_FAILED("500_RECOMMEND_TAGS_DATA_PROCESSING_FAILED", "추천 태그 데이터를 처리하는 중 오류가 발생했습니다.");



    private final String code;
    private final String msg;
}
