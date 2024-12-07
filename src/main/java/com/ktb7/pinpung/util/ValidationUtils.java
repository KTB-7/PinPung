package com.ktb7.pinpung.util;

import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@AllArgsConstructor
public class ValidationUtils {

    private final UserRepository userRepository;

//     x, y 좌표 값의 유효성을 검사하는 메서드
    public static void validateCoordinates(String x, String y) {
        try {
            double xCoord = Double.parseDouble(x);
            double yCoord = Double.parseDouble(y);

            // 한국 좌표인지 확인
            if (xCoord < 123 || xCoord > 133 || yCoord < 32 || yCoord > 44) {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
            }
        } catch (NumberFormatException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
        }
    }

    // rect 값의 유효성을 검사하는 메서드
    public static void validateRect(String swLng, String swLat, String neLng, String neLat) {
        try {
            double swLngValue = Double.parseDouble(swLng);
            double swLatValue = Double.parseDouble(swLat);
            double neLngValue = Double.parseDouble(neLng);
            double neLatValue = Double.parseDouble(neLat);

            if (swLngValue < 123 || swLngValue > 133 || swLatValue < 32 || swLatValue > 44 ||
                    neLngValue < 123 || neLngValue > 133 || neLatValue < 32 || neLatValue > 44) {
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "좌표 범위가 유효하지 않습니다.");
            }
        } catch (NumberFormatException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, "좌표 값은 숫자로 변환할 수 있어야 합니다.");
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
//        if (placeIds.size() > 100) {
//            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
//        }
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

    public static void validateKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER);
        }
    }

    public static void validateUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER);
        }
    }

    public static void validateListString(List<String> list) {
        if (list == null || list.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER);
        }
    }

    public static void validateAge(Integer age) {
        if (age == null || age <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
        }
    }
}
