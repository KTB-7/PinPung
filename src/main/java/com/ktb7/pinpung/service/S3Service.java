package com.ktb7.pinpung.service;

import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public Map<String, String> uploadFile(MultipartFile imageWithText, MultipartFile pureImage, Long imageId) {
        try {
            String imageTextKey = "uploaded-images/" + imageId;
            String pureImageKey = "original-images/" + imageId;

            log.info("S3 업로드 시작 - bucket: {}, imageTextKey: {}, pureImageKey: {}", bucketName, imageTextKey, pureImageKey);

            // imageWithText 파일을 임시 파일로 변환 후 업로드
            Path tempImageTextFile = Files.createTempFile("temp-" + imageWithText.getOriginalFilename(), null);
            imageWithText.transferTo(tempImageTextFile);
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(imageTextKey)
                            .build(),
                    tempImageTextFile);

            // pureImage 파일을 임시 파일로 변환 후 업로드
            Path tempPureImageFile = Files.createTempFile("temp-" + pureImage.getOriginalFilename(), null);
            pureImage.transferTo(tempPureImageFile);
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(pureImageKey)
                            .build(),
                    tempPureImageFile);

            // 업로드 후 임시 파일 삭제
            Files.deleteIfExists(tempImageTextFile);
            Files.deleteIfExists(tempPureImageFile);

            Map<String, String> imageKeys = new HashMap<>();
            imageKeys.put("imageTextKey", imageTextKey);
            imageKeys.put("pureImageKey", pureImageKey);

            return imageKeys;
        } catch (IOException e) {
            log.error("파일 생성 또는 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_PROCESSING_FAILED, "파일 생성 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("이미지 업로드 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.IMAGE_UPLOAD_FAILED, "이미지 업로드 중 오류가 발생했습니다.");
        }
    }
}