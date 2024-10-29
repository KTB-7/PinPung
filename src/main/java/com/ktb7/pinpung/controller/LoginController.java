//package com.ktb7.pinpung.controller;
//
//import com.ktb7.pinpung.entity.User;
//import com.ktb7.pinpung.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/loginSuccess")
//public class LoginController {
//
//    private final UserRepository userRepository;
//
//    @GetMapping
//    public User getUserInfo(@AuthenticationPrincipal OAuth2User oAuth2User) {
//        Long kakaoId = (Long) oAuth2User.getAttribute("id");
//        log.info("{}", kakaoId);
//        return userRepository.findBySocialId(kakaoId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//    }
//}
