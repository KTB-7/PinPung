package com.ktb7.pinpung.util;

import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ValidationUtils {

    // x, y 좌표 값의 유효성을 검사하는 메서드
    public static void validateCoordinates(String x, String y) {
        try {
            double xCoord = Double.parseDouble(x);
            double yCoord = Double.parseDouble(y);

            if (xCoord < -180 || xCoord > 180 || yCoord < -90 || yCoord > 90) {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "유효하지 않은 좌표 값입니다.");
            }
        } catch (NumberFormatException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "좌표 값은 숫자 형식이어야 합니다.");
        }
    }

    // 반경 값의 유효성을 검사하는 메서드
    public static void validateRadius(Integer radius) {
        if (radius == null || radius <= 0 || radius > 10000) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "반경 값은 0보다 크고 10000 이하이어야 합니다.");
        }
    }

    // placeId의 유효성을 검사하는 메서드
    public static void validatePlaceId(Long placeId) {
        if (placeId == null || placeId <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "유효하지 않은 장소 ID입니다.");
        }
    }

    // placeIds 리스트의 유효성을 검사하는 메서드
    public static void validatePlaceIds(List<Long> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER, "장소 ID 리스트가 누락되었습니다.");
        }
        if (placeIds.size() > 100) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "장소 ID 리스트의 크기는 최대 100개까지 허용됩니다.");
        }
    }

    // userId와 placeId의 유효성을 검사하는 메서드
    public static void validateUserAndPlaceId(Long userId, Long placeId) {
        validateUserId(userId);
        validatePlaceId(placeId);
    }

    // MultipartFile이 비어있는지 검사하는 메서드
    public static void validateFile(MultipartFile file, String fileName) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER, fileName + " 파일이 누락되었습니다.");
        }
    }

    // 페이지와 사이즈 값의 유효성을 검사하는 메서드
    public static void validatePagination(int page, int size) {
        if (page < 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "페이지 번호는 0 이상이어야 합니다.");
        }
        if (size <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "페이지 크기는 1 이상이어야 합니다.");
        }
    }

    // userId의 유효성을 검사하는 메서드 (양수인지 확인)
    public static void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "유효하지 않은 사용자 ID입니다.");
        }
    }

    // AccessToken의 유효성을 검사하는 메서드 (null 또는 빈 문자열인지 확인)
    public static void validateAccessToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER, "액세스 토큰이 누락되었습니다.");
        }
    }

    // 로그인된 사용자 ID와 요청된 userId가 일치하는지 확인하는 메서드
    public static void validateUserRequest(Long requestUserId) {
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(requestUserId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, ErrorCode.UNAUTHORIZED_CLIENT, "접근 권한이 없습니다.");
        }
    }

    // 현재 로그인된 사용자 ID를 반환하는 메서드
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User user = (OAuth2User) authentication.getPrincipal();
            return Long.parseLong(user.getAttribute("id")); // OAuth2 사용자 ID 가져오기
        }
        throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.AUTHENTICATION_FAILED, "인증되지 않은 사용자입니다.");
    }
}
