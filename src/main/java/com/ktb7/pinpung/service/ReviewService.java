package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.Review.UploadReviewRequestDto;
import com.ktb7.pinpung.dto.Review.ModifyReviewRequestDto;
import com.ktb7.pinpung.dto.Review.DeleteReviewRequestDto;
import com.ktb7.pinpung.dto.Review.MessageResponseDto;
import com.ktb7.pinpung.entity.Image;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.ImageRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import com.ktb7.pinpung.util.RepositoryHelper;
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
    private final RepositoryHelper repositoryHelper;
    private final AiService aiService;

    @Transactional
    public MessageResponseDto uploadReview(UploadReviewRequestDto uploadReviewRequest) {
        Long userId = uploadReviewRequest.getUserId();
        Long placeId = uploadReviewRequest.getPlaceId();
        String text = uploadReviewRequest.getText();
        MultipartFile reviewImage = uploadReviewRequest.getReviewImage();

        try {
            Long imageId = null;

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

            // 2. Review 엔티티 생성 후 저장 (imageId가 없으면 null로 저장됨)
            Review review = new Review();
            review.setUserId(userId);
            review.setPlaceId(placeId);
            review.setImageId(imageId);
            review.setText(text);
            reviewRepository.save(review);

            // 5. AI에 이미지 전달 (실패해도 프론트엔드에 영향 없음)
            try {
                aiService.genTags(placeId, text, "https://pinpung-s3.s3.ap-northeast-2.amazonaws.com/original-images/"+imageId, userId);
                log.info("AI 태그 생성 요청 완료");
            } catch (Exception aiException) {
                log.error("AI 태그 생성 중 오류 발생: {}", aiException.getMessage(), aiException);
            }

            return new MessageResponseDto("Review upload success");

        } catch (Exception e) {
            log.error("이미지 업로드 및 review 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, "review 업로드 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public MessageResponseDto modifyReview(ModifyReviewRequestDto modifyReviewRequest) {
        Long userId = modifyReviewRequest.getUserId();
        Long reviewId = modifyReviewRequest.getReviewId();
        Long placeId = modifyReviewRequest.getPlaceId();
        String text = modifyReviewRequest.getText();
        MultipartFile reviewImage = modifyReviewRequest.getReviewImage();

        // 유일한 리뷰 조회
        Review review = repositoryHelper.findUniqueReview(userId, placeId, reviewId);
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

        return new MessageResponseDto("Review modify success");
    }

    @Transactional
    public MessageResponseDto deleteReview(DeleteReviewRequestDto deleteReviewRequest) {
        Long userId = deleteReviewRequest.getUserId();
        Long reviewId = deleteReviewRequest.getReviewId();
        Long placeId = deleteReviewRequest.getPlaceId();

        // 유일한 리뷰 조회
        Review review = repositoryHelper.findUniqueReview(userId, placeId, reviewId);
        Long imageId = review.getImageId();

        // 리뷰 삭제
        reviewRepository.delete(review);

        // 삭제 여부 확인
        boolean existsAfterDelete = reviewRepository.existsById(reviewId);
        if (existsAfterDelete) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, "리뷰 삭제에 실패했습니다.");
        }
        return new MessageResponseDto("Review delete success");
    }
}
