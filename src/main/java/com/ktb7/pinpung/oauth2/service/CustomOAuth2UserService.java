package com.ktb7.pinpung.oauth2.service;
import com.ktb7.pinpung.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오 사용자 정보 가져오기
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Long kakaoId = Long.parseLong(String.valueOf(attributes.get("id")));
        log.info("User info loaded with Kakao ID: {}", kakaoId);

        return oAuth2User;
    }
}
