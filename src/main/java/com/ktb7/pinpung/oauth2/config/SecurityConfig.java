package com.ktb7.pinpung.oauth2.config;

import com.ktb7.pinpung.oauth2.OAuth2LoginFailureHandler;
import com.ktb7.pinpung.oauth2.OAuth2LoginSuccessHandler;
import com.ktb7.pinpung.oauth2.OAuth2LogoutCustomHandler;
import com.ktb7.pinpung.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final OAuth2LogoutCustomHandler oAuth2LogoutCustomHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/oauth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                                .successHandler(oAuth2LoginSuccessHandler)
//                        .failureHandler(oAuth2LoginFailureHandler)
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)
                                )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(oAuth2LogoutCustomHandler)
                        .logoutSuccessUrl("/login") // 카카오 로그아웃 후 로그인 페이지로 리다이렉트
                );
        return http.build();
    }
}
