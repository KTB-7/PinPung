package com.ktb7.pinpung.oauth2.config;

import com.ktb7.pinpung.oauth2.OAuth2LoginFailureHandler;
import com.ktb7.pinpung.oauth2.OAuth2LoginSuccessHandler;
import com.ktb7.pinpung.oauth2.OAuth2LogoutCustomHandler;
import com.ktb7.pinpung.oauth2.OAuth2LogoutSuccessHandler;
import com.ktb7.pinpung.oauth2.service.CustomOAuth2UserService;
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
    private final OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 로그인 필요 없음 (permitAll)
                        .requestMatchers("/login", "/logout-success", "/places/nearby", "/places/{placeId}", "/pungs/{placeId}", "/places/tag-reviews").permitAll()

                        // 로그인 필요 (authenticated)
                        .requestMatchers("/pungs", "/reviews", "/logout").authenticated()

                        .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(oAuth2LogoutCustomHandler)
                        .logoutSuccessHandler(oAuth2LogoutSuccessHandler)
                );
        return http.build();
    }
}
