package com.ktb7.pinpung.service;

import org.springframework.beans.factory.annotation.Value;
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

    // 파일 업로드 (imageId를 파일명으로 사용)
    public Map<String, String> uploadFile(MultipartFile imageWithText, MultipartFile pureImage, Long imageId) throws IOException {
        String imageTextKey = "images/withText/" + imageId; // imageId를 파일명으로 사용
        String pureImageKey = "images/pure/" + imageId;

        // PutObjectRequest: imageWithText
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(imageTextKey)
                        .build(),
                Paths.get(Files.createTempFile(imageWithText.getOriginalFilename(), null).toString()));

        // PutObjectRequest: pureImage
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(pureImageKey)
                        .build(),
                Paths.get(Files.createTempFile(pureImage.getOriginalFilename(), null).toString()));

        // S3에 저장된 파일 경로들을 반환
        Map<String, String> imageKeys = new HashMap<>();
        imageKeys.put("imageTextKey", imageTextKey);
        imageKeys.put("pureImageKey", pureImageKey);

        return imageKeys;
    }
}