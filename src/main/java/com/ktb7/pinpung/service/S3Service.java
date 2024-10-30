package com.ktb7.pinpung.service;

import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
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

            // 이미지 파일을 임시 파일로 변환 및 S3 업로드
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(imageTextKey)
                            .build(),
                    Paths.get(Files.createTempFile(imageWithText.getOriginalFilename(), null).toString()));

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(pureImageKey)
                            .build(),
                    Paths.get(Files.createTempFile(pureImage.getOriginalFilename(), null).toString()));

            Map<String, String> imageKeys = new HashMap<>();
            imageKeys.put("imageTextKey", imageTextKey);
            imageKeys.put("pureImageKey", pureImageKey);

            return imageKeys;
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.FILE_PROCESSING_FAILED, "파일 생성 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.IMAGE_UPLOAD_FAILED, "이미지 업로드 중 오류가 발생했습니다.");
        }
    }
}
