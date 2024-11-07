package com.ktb7.pinpung.oauth2.config;

import com.ktb7.pinpung.oauth2.OAuth2LoginFailureHandler;
import com.ktb7.pinpung.oauth2.OAuth2LoginSuccessHandler;
import com.ktb7.pinpung.oauth2.OAuth2LogoutCustomHandler;
import com.ktb7.pinpung.oauth2.service.CustomOAuth2UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
//@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final OAuth2LogoutCustomHandler oAuth2LogoutCustomHandler;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "https://pinpung.net", "https://www.pinpung.net"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Applying Security Filter Chain");
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        // 로그인 필요 없음 (permitAll)
                        .requestMatchers("/login", "/api/test", "/logout-success", "/api/places/nearby", "/api/places/{placeId}", "/api/pungs/{placeId}", "/api/places/tag-reviews").permitAll()

                        // 로그인 필요 (authenticated)
                        .requestMatchers("/api/reviews/upload", "/logout", "/api/pungs/upload", "/api/reviews/modify").authenticated()

                        .anyRequest().authenticated()  // 나머지 요청도 인증 필요
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
                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(securityContext -> securityContext
                        .requireExplicitSave(true)
                )
                .addFilterBefore(new KakaoTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

//                .securityMatcher("/actuator/health");

        return http.build();
    }

}
