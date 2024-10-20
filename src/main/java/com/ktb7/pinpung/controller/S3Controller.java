//package com.ktb7.pinpung.controller;
//
//import com.ktb7.pinpung.service.S3Service;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.InputStream;
//
//@RestController
//@RequestMapping("/s3")
//public class S3Controller {
//
//    private final S3Service s3Service;
//
//    public S3Controller(S3Service s3Service) {
//        this.s3Service = s3Service;
//    }
//
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            String key = s3Service.uploadFile(file);
//            return ResponseEntity.ok("File uploaded successfully. Key: " + key);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/download")
//    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("key") String key) {
//        try {
//            // S3에서 파일을 스트림으로 가져옴
//            InputStream fileStream = s3Service.downloadFileAsStream(key);
//
//            // 파일을 클라이언트로 전송
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key) // 파일 이름을 헤더에 추가
//                    .contentType(MediaType.IMAGE_JPEG) // 파일 형식을 적절히 설정 (예: 이미지인 경우)
//                    .body(new InputStreamResource(fileStream)); // 파일 스트림을 응답 본문에 포함
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }
//}
