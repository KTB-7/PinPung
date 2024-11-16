package com.ktb7.pinpung.oauth2.config;

import com.ktb7.pinpung.oauth2.OAuth2LoginFailureHandler;
import com.ktb7.pinpung.oauth2.OAuth2LoginSuccessHandler;
import com.ktb7.pinpung.oauth2.OAuth2LogoutCustomHandler;
import com.ktb7.pinpung.oauth2.service.CustomOAuth2UserService;
import com.ktb7.pinpung.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final OAuth2LogoutCustomHandler oAuth2LogoutCustomHandler;
    private final UserRepository userRepository;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "https://pinpung.net", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 필터체인 1: 검증이 필요 없는 경로
     */
    @Bean
    @Order(1)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Applying Public Security Filter Chain!");

        http
                .securityMatcher(
                        "/logout-success",
                        "/api/test",
                        "/api/places/**",
                        "/api/pungs/{placeId}",
                        "/actuator/health",
                        "/favicon.ico",
                        "/login"
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure() // HTTPS 강제
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * 필터체인 2: 검증이 필요한 경로
     */
    @Bean
    @Order(2)
    public SecurityFilterChain authenticatedSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Applying Authenticated Security Filter Chain!");

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/reviews",
                                "/api/follows",
                                "/logout",
                                "/api/pungs/upload"
                        ).authenticated()
                        .anyRequest().authenticated()
                )
                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure() // HTTPS 강제
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/logout-success")
                        .addLogoutHandler(oAuth2LogoutCustomHandler)
                )
                .addFilterBefore(new KakaoTokenAuthenticationFilter(userRepository), AnonymousAuthenticationFilter.class);

        return http.build();
    }
}