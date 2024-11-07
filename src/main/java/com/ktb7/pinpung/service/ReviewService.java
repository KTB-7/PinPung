package com.ktb7.pinpung.service;

import com.ktb7.pinpung.entity.Image;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.ImageRepository;
import com.ktb7.pinpung.repository.PungRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ReviewService {

    private final ImageRepository imageRepository;
    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    @Transactional
    public void uploadReview(Long userId, Long placeId, MultipartFile reviewImage, String text) {
        log.info("uploadReview 호출됨: userId={}, placeId={}, text={}", userId, placeId, text);
        try {
            Long imageId = null;

            // 1. 이미지가 존재할 때만 Image 엔티티 생성 및 S3 업로드 수행
            if (reviewImage != null && !reviewImage.isEmpty()) {
                Image image = new Image();
                imageRepository.save(image);
                imageId = image.getImageId();

                // S3에 이미지 업로드
                Map<String, String> imageKeys = s3Service.uploadFile(null, reviewImage, imageId, true);
//                image.setImageTextKey(imageKeys.get("imageTextKey"));
                image.setPureImageKey(imageKeys.get("pureImageKey"));
                imageRepository.save(image);
            }

            // 2. Review 엔티티 생성 후 저장 (imageId가 없으면 null로 저장됨)
            Review review = new Review();
            review.setUserId(userId);
            review.setPlaceId(placeId);
            review.setImageId(imageId);  // 이미지가 없을 경우 null로 설정
            review.setText(text);
            reviewRepository.save(review);
        } catch (Exception e) {
            log.error("리뷰 업로드 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, "리뷰 업로드 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void modifyReview(Long userId, Long reviewId, Long placeId, MultipartFile reviewImage, String text) {

        // 해당 리뷰가 정확히 하나 존재하는지 확인
        List<Review> reviews = reviewRepository.findByUserIdAndPlaceIdAndReviewId(userId, placeId, reviewId);

        if (reviews.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.REVIEW_NOT_FOUND, ErrorCode.REVIEW_NOT_FOUND.getMsg());
        } else if (reviews.size() > 1) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, ErrorCode.DATABASE_ERROR.getMsg());
        }

        Review review = reviews.get(0);

        try {
            Long imageId = review.getImageId();

            // 1. 이미지가 존재할 때만 Image 엔티티 생성 및 S3 업로드 수행
            if (reviewImage != null && !reviewImage.isEmpty()) {
                Image image = new Image();
                imageRepository.save(image);
                imageId = image.getImageId();

                // S3에 이미지 업로드
                Map<String, String> imageKeys = s3Service.uploadFile(null, reviewImage, imageId, true);
                image.setPureImageKey(imageKeys.get("pureImageKey"));
                imageRepository.save(image);
            }

            // 텍스트 및 이미지 ID 업데이트
            review.setText(text);
            review.setImageId(imageId);

        } catch (Exception e) {
            log.error("리뷰 수정 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, ErrorCode.FILE_UPLOAD_FAILED.getMsg());
        }
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId, Long placeId) {
        // 해당 리뷰가 정확히 하나 존재하는지 확인
        List<Review> reviews = reviewRepository.findByUserIdAndPlaceIdAndReviewId(userId, placeId, reviewId);

        if (reviews.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, ErrorCode.REVIEW_NOT_FOUND, ErrorCode.REVIEW_NOT_FOUND.getMsg());
        } else if (reviews.size() > 1) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, ErrorCode.DATABASE_ERROR.getMsg());
        }

        Review review = reviews.get(0);

        try {
            reviewRepository.delete(review);
        } catch (Exception e) {
            log.error("리뷰 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, ErrorCode.DATABASE_ERROR.getMsg());
        }
    }
}
