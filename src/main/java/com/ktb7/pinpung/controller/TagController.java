//package com.ktb7.pinpung.controller;
//
//import com.ktb7.pinpung.oauth2.service.TokenService;
//import com.ktb7.pinpung.service.PlaceService;
//import com.ktb7.pinpung.service.TagService;
//import com.ktb7.pinpung.util.ValidationUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.coyote.Request;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/tags")
//@Slf4j
//@RequiredArgsConstructor
//public class TagController {
//
//    private final TagService tagService;
//    private final TokenService tokenService;
//
//    @GetMapping
//    public ResponseEntity<List<String>> getTags(@RequestHeader("Authorization") String authorizationHeader) {
//
//        log.info("Received request to /tags");
//
//        String token = tokenService.extractBearerToken(authorizationHeader);
//        Long userId = tokenService.getUserFromToken(token);
//
//        ValidationUtils.validateUserId(userId);
//
//        List<String> response = tagService.getTags();
//
//        return ResponseEntity.ok(response);
//    }
//
//}
