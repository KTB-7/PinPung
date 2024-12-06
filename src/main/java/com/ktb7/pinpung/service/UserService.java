package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.Profile.*;
import com.ktb7.pinpung.dto.Review.MessageResponseDto;
import com.ktb7.pinpung.dto.User.TasteRequestDto;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.entity.UserActivity;
import com.ktb7.pinpung.entity.UserMenu;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PungRepository pungRepository;
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final UserActivityRepository userActivityRepository;
    private final UserMenuRepository userMenuRepository;


    public ProfileWithPungResponseDto viewProfileWithPung(Long userId) {

        DefaultProfileDto defaultProfileDto = getDefaultProfile(userId);

        List<SimplePung> simplePungs = pungRepository.findSimplePungByUserId(userId);

        return new ProfileWithPungResponseDto(defaultProfileDto, simplePungs);

    }

    public ProfileWithReviewResponseDto viewProfileWithReview(Long userId) {

        DefaultProfileDto defaultProfileDto = getDefaultProfile(userId);

        List<Review> reviews = reviewRepository.findByUserId(userId);

        List<Long> placeIds = reviews.stream()
                .map(Review::getPlaceId)
                .distinct()
                .toList();

        Map<Long, String> placeIdToNameMap = placeRepository.findPlaceNamesByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (String) result[1]
                ));

        List<SimpleReview> simpleReviews = reviews.stream()
                .map(review -> new SimpleReview(
                        review.getReviewId(),
                        userId,
                        review.getPlaceId(),
                        placeIdToNameMap.get(review.getPlaceId()),
                        review.getImageId(),
                        review.getText(),
                        review.getUpdatedAt()
                ))
                .toList();

        return new ProfileWithReviewResponseDto(defaultProfileDto, simpleReviews);

    }

    public DefaultProfileDto getDefaultProfile(Long userId) {
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        // 팔로워 및 팔로잉 정보 조회
        List<User> followers = followRepository.findFollowersByUserId(userId);
        List<User> followings = followRepository.findFollowingsByUserId(userId);

        Long reviewCount = reviewRepository.countByUserId(userId);
        Long pungCount = pungRepository.countByUserId(userId);

        // DefaultProfileDto 반환
        return new DefaultProfileDto(
                user.getUserId(),
                user.getUserName(),
                followers.size(),
                followings.size(),
                pungCount,
                reviewCount
        );
    }

    public MessageResponseDto setTaste(Long userId, TasteRequestDto tasteRequestDto) {
        try {
            Integer age = tasteRequestDto.getAge();
            List<String> activities = tasteRequestDto.getActivities();
            List<String> menus = tasteRequestDto.getMenus();

            // 사용자 조회 및 나이 설정
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
            user.setAge(age);
            userRepository.save(user); // 사용자 정보 업데이트

            // 활동 저장
            activities.forEach(activity -> {
                UserActivity userActivity = new UserActivity();
                userActivity.setUserId(userId);
                userActivity.setActivityName(activity);
                userActivityRepository.save(userActivity);
            });

            // 메뉴 저장
            menus.forEach(menu -> {
                UserMenu userMenu = new UserMenu();
                userMenu.setUserId(userId);
                userMenu.setMenuName(menu);
                userMenuRepository.save(userMenu);
            });

            return new MessageResponseDto("사용자 취향 저장 성공");

        } catch (CustomException e) {
            log.error("CustomException 발생: {}", e.getMessage(), e);
            throw new CustomException(e.getStatus(), e.getErrorCode(), "사용자 취향 저장 중 에러 발생");
        } catch (Exception e) {
            log.error("Unexpected error 발생: {}", e.getMessage(), e);
            throw new RuntimeException("unexpected error while setting taste.", e);
        }
    }
}
