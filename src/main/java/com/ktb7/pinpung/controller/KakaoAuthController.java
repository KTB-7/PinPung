//package com.ktb7.pinpung.controller;
//
//import com.ktb7.pinpung.oauth2.dto.KakaoTokenResponseDto;
//import com.ktb7.pinpung.oauth2.dto.LoginResponseDto;
//import com.ktb7.pinpung.oauth2.dto.TokenResponseDto;
//import com.ktb7.pinpung.entity.Token;
//import com.ktb7.pinpung.repository.TokenRepository;
//import com.ktb7.pinpung.service.KakaoService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.util.Optional;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/oauth")
//public class KakaoAuthController {
//
//    private final KakaoService kakaoService;
//    private final TokenRepository tokenRepository;
//
//    // 인가 코드 받아서 토큰 발급받고 유저 정보 저장
//    @GetMapping("/callback")
//    public ResponseEntity<LoginResponseDto> callback(@RequestParam("code") String code) throws IOException {
//        log.info("{}", code);
//        LoginResponseDto loginResponse = kakaoService.getAccessTokenAndSave(code);
//        log.info("{}", loginResponse);
//        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
//    }
//
//    // 토큰 갱신
//    @GetMapping("/refresh-token")
//    public ResponseEntity<TokenResponseDto> refreshToken(@RequestParam("userId") Long userId) {
//        // 유저 ID로 리프레시 토큰 조회
//        Optional<Token> tokenOptional = tokenRepository.findByUserId(userId);
//
//        if (!tokenOptional.isPresent()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 리프레시 토큰이 없으면 404 응답
//        }
//
//        Token token = tokenOptional.get();
//        KakaoTokenResponseDto newTokenResponse = kakaoService.refreshAccessToken(token.getRefreshToken());
//
//        // 새로운 액세스 토큰과 만료 시간을 토큰 테이블에 업데이트
//        token.setAccessToken(newTokenResponse.getAccessToken());
//        token.setExpiresIn(newTokenResponse.getExpiresIn());
//        tokenRepository.save(token);
//
//        // 프론트에는 리프레시 토큰 갱신 결과만 전달
//        return new ResponseEntity<>(new TokenResponseDto(null, newTokenResponse.getExpiresIn()), HttpStatus.OK);
//    }
//}
