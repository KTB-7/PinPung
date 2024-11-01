package com.ktb7.pinpung.util;

import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@AllArgsConstructor
public class ValidationUtils {

    private final UserRepository userRepository;

    // x, y 좌표 값의 유효성을 검사하는 메서드
    public static void validateCoordinates(String x, String y) {
        try {
            double xCoord = Double.parseDouble(x);
            double yCoord = Double.parseDouble(y);

            if (xCoord < -180 || xCoord > 180 || yCoord < -90 || yCoord > 90) {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
            }
        } catch (NumberFormatException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
        }
    }

    // 반경 값의 유효성을 검사하는 메서드
    public static void validateRadius(Integer radius) {
        if (radius == null || radius <= 0 || radius > 10000) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
        }
    }

    // placeId의 유효성을 검사하는 메서드
    public static void validatePlaceId(Long placeId) {
        if (placeId == null || placeId <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
        }
    }

    // placeIds 리스트의 유효성을 검사하는 메서드
    public static void validatePlaceIds(List<Long> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER);
        }
        if (placeIds.size() > 100) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
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
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER);
        }
    }

    // 페이지와 사이즈 값의 유효성을 검사하는 메서드
    public static void validatePagination(int page, int size) {
        if (page < 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
        }
        if (size <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
        }
    }

    // userId의 유효성을 검사하는 메서드 (양수인지 확인)
    public static void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
        }
    }

    // AccessToken의 유효성을 검사하는 메서드 (null 또는 빈 문자열인지 확인)
    public static void validateAccessToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER);
        }
    }

//    // 로그인된 사용자 ID와 요청된 userId가 일치하는지 확인하는 메서드
//    public void validateUserRequest(Long requestUserId) {
//        Long currentSocialId = getCurrentUserId();
//        User user = userRepository.findBySocialId(currentSocialId)
//                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND));
//
//        if (!user.getUserId().equals(requestUserId)) {
//            throw new CustomException(HttpStatus.FORBIDDEN, ErrorCode.UNAUTHORIZED_CLIENT);
//        }
//    }
//
//    // 현재 로그인된 사용자 ID를 반환하는 메서드
//    public Long getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
//            OAuth2User user = (OAuth2User) authentication.getPrincipal();
//            return Long.parseLong(user.getAttribute("id"));
//        }
//        throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.AUTHENTICATION_FAILED);
//    }
}
