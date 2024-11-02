package com.ktb7.pinpung.oauth2.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(1) // 우선 순위를 높게 설정
public class ActuatorSecurityConfig {

    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/actuator/**") // /actuator 경로만 해당 설정을 사용
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 /actuator 경로에 대해 인증 없이 허용
                )
                .csrf(csrf -> csrf.disable()); // CSRF 비활성화

        return http.build();
    }
}