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
            Boolean isReview = true;

            // 1. Image 엔티티 생성 후 저장하여 imageId 얻기
            Image image = new Image();
            imageRepository.save(image);
            Long imageId = image.getImageId();

            // 2. S3에 이미지 업로드
            Map<String, String> imageKeys = s3Service.uploadFile(null, reviewImage, imageId, isReview);
            System.out.println(imageKeys);

            // 3. Image 엔티티에 S3 키값 업데이트 후 저장
            image.setImageTextKey(imageKeys.get("imageTextKey"));
            image.setPureImageKey(imageKeys.get("pureImageKey"));
            imageRepository.save(image);

            // 4. Pung 엔티티 생성 후 저장
            Review review = new Review();
            review.setUserId(userId);
            review.setPlaceId(placeId);
            review.setImageId(imageId);
            review.setText(text);
            reviewRepository.save(review);
        } catch (Exception e) {
            log.error("이미지 업로드 및 Pung 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, "Pung 업로드 중 오류가 발생했습니다.");
        }
    }
}
