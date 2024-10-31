package com.ktb7.pinpung.service;

import com.ktb7.pinpung.entity.Image;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.ImageRepository;
import com.ktb7.pinpung.repository.PungRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class PungService {
    private final PungRepository pungRepository;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;
    private final Clock clock;

    public PungsResponseDto getPungsByPlaceId(Long placeId, Pageable pageable) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);
        Page<Pung> pungsPage = pungRepository.findByPlaceIdAndCreatedAtAfter(placeId, yesterday, pageable);

        int pungCount = (int) pungsPage.getTotalElements();
        int currentPage = pungsPage.getNumber();
        log.info("pungs/{placeId} pungCount, currentPage: {} {}", pungCount, currentPage);

        return new PungsResponseDto(pungCount, currentPage, pungsPage.getContent());
    }


    @Transactional
    public void uploadPung(Long userId, Long placeId, MultipartFile imageWithText, MultipartFile pureImage, String text) {
        log.info("uploadPung 호출됨: userId={}, placeId={}, text={}", userId, placeId, text);
        try {

            // 1. Image 엔티티 생성 후 저장하여 imageId 얻기
            Image image = new Image();
            imageRepository.save(image);
            Long imageId = image.getImageId();

            // 2. S3에 이미지 업로드
            Map<String, String> imageKeys = s3Service.uploadFile(imageWithText, pureImage, imageId);
            System.out.println(imageKeys);

            // 3. Image 엔티티에 S3 키값 업데이트 후 저장
            image.setImageTextKey(imageKeys.get("imageTextKey"));
            image.setPureImageKey(imageKeys.get("pureImageKey"));
            imageRepository.save(image);

            // 4. Pung 엔티티 생성 후 저장
            Pung pung = new Pung();
            pung.setUserId(userId);
            pung.setPlaceId(placeId);
            pung.setImageWithText(imageId);
            pung.setText(text);
            pungRepository.save(pung);
        } catch (Exception e) {
            log.error("이미지 업로드 및 Pung 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_UPLOAD_FAILED, "Pung 업로드 중 오류가 발생했습니다.");
        }
    }
}
