//package com.ktb7.pinpung.exception.common;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//
//@Getter
//@RequiredArgsConstructor
//public enum ErrorCode {
//
//    // 400 Bad Request
//    UNKNOWN("000_UNKNOWN", "알 수 없는 에러가 발생했습니다."),
//    BAD_REQUEST("400_BAD_REQUEST", "잘못된 요청입니다."),
//    VALIDATION_FAILED("400_VALIDATION_FAILED", "유효성 검증에 실패했습니다."),
//    MISSING_PARAMETER("400_MISSING_PARAMETER", "필수 파라미터가 누락되었습니다."),
//    INVALID_PARAMETER("400_INVALID_PARAMETER", "잘못된 파라미터가 포함되었습니다."),
//    DUPLICATED_RESOURCE("400_DUPLICATED_RESOURCE", "이미 존재하는 리소스입니다."),
//    RESOURCE_NOT_FOUND("400_RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
//    INVALID_PLACE_ID("400_INVALID_PLACE_ID", "유효하지 않은 장소 ID입니다."),
//
//    // 404 Not Found
//    PLACE_NOT_FOUND("404_PLACE_NOT_FOUND", "해당 장소를 찾을 수 없습니다."),
//    REVIEW_NOT_FOUND("404_REVIEW_NOT_FOUND", "해당 리뷰를 찾을 수 없습니다."),
//    TAG_NOT_FOUND("404_TAG_NOT_FOUND", "해당 태그를 찾을 수 없습니다."),
//
//    // 500 Internal Server Error
//    INTERNAL_SERVER_ERROR("500_INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
//    DATABASE_ERROR("500_DATABASE_ERROR", "데이터베이스 처리 중 오류가 발생했습니다."),
//    FILE_UPLOAD_FAILED("500_FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),
//    S3_UPLOAD_FAILED("500_S3_UPLOAD_FAILED", "S3에 파일 업로드 중 오류가 발생했습니다."),
//    IMAGE_PROCESSING_FAILED("500_IMAGE_PROCESSING_FAILED", "이미지 처리 중 오류가 발생했습니다."),
//    UNEXPECTED_ERROR("500_UNEXPECTED_ERROR", "예상치 못한 오류가 발생했습니다.");
//
//    private final String code;
//    private final String msg;
//}
