package com.ktb7.pinpung.service;

import com.ktb7.pinpung.entity.Image;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.repository.ImageRepository;
import com.ktb7.pinpung.repository.PungRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    /*
   GET pungs/{placeId}
   place id를 받아 해당 장소의 펑 모음 반환
   */
    public PungsResponseDto getPungsByPlaceId(Long placeId, Pageable pageable) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);
        Page<Pung> pungsPage = pungRepository.findByPlaceIdAndCreatedAtAfter(placeId, yesterday, pageable);

        int pungCount = (int) pungsPage.getTotalElements();
        int currentPage = pungsPage.getNumber();
        log.info("pungs/{placeId} pungCount, currentPage: {} {}", pungCount, currentPage);

        return new PungsResponseDto(pungCount, currentPage, pungsPage.getContent());
    }

    @Transactional
    public void uploadPung(Long userId, Long placeId, MultipartFile imageWithText, MultipartFile pureImage, String text) throws Exception {
        // 1. Image 엔티티 저장 (먼저 Image 엔티티를 저장해 imageId를 얻음)
        Image image = new Image();  // Image 엔티티 생성
        imageRepository.save(image);  // Image를 저장하고 imageId 자동 생성

        Long imageId = image.getImageId(); // DB에서 자동 생성된 imageId 얻음

        // 2. S3에 이미지 업로드 (imageId를 파일 이름으로 사용)
        Map<String, String> imageKeys = s3Service.uploadFile(imageWithText, pureImage, imageId);

        // 3. Image 엔티티에 S3 키값 업데이트 후 저장
        image.setImageTextKey(imageKeys.get("imageTextKey"));
        image.setPureImageKey(imageKeys.get("pureImageKey"));
        imageRepository.save(image);  // 업데이트된 값으로 다시 저장

        // 4. Pung 엔티티 저장
        Pung pung = new Pung();
        pung.setUserId(userId);
        pung.setPlaceId(placeId);
        pung.setImageWithText(imageId);
        pung.setText(text);
        pungRepository.save(pung);
    }
}