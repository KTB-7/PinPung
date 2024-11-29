//package com.ktb7.pinpung.controller;
//
//import com.ktb7.pinpung.dto.AI.RecommendTagsResponse;
//import com.ktb7.pinpung.service.AiService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/ai")
//@Slf4j
//public class AiController {
//
//    private AiService aiService;
//
//    @GetMapping("/recommend/{userId}")
//    public ResponseEntity<RecommendTagsResponse> recommend(
//            @PathVariable Long userId,
//            @RequestParam String swLng,
//            @RequestParam String swLat,
//            @RequestParam String neLng,
//            @RequestParam String neLat,
//            @RequestParam String x,
//            @RequestParam String y
//    ) {
//        aiService.recommend();
//
//
//
//
//
//
//    }
//}
