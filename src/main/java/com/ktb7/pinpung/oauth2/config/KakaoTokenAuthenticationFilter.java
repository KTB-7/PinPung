package com.ktb7.pinpung.oauth2.config;

import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.oauth2.dto.KakaoTokenInfoResponseDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class KakaoTokenAuthenticationFilter extends OncePerRequestFilter {

    private final String kakaoUserInfoUrl = "https://kapi.kakao.com/v1/user/access_token_info";
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info("{}", token);

            try {
                // socialId로 userId 찾기
                Long socialId = validateTokenAndExtractUserId(token);
                log.info("Validate user id {}", socialId);
                User user = userRepository.findBySocialId(socialId)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

                Long userId = user.getUserId();
                if (userId != null) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (CustomException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // GET 요청의 경우 필터가 적용되지 않도록 설정
        if (requestURI.startsWith("/api/pungs/") && "GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 로그인 없이 접근 가능한 기타 URL 경로 설정
        return requestURI.startsWith("/login") ||
                requestURI.startsWith("/oauth2/authorization/kakao") ||
                requestURI.startsWith("/api/places/nearby") ||
                requestURI.startsWith("/api/places") ||
                requestURI.startsWith("/favicon.ico") ||
                requestURI.startsWith("/logout-success") ||
                requestURI.startsWith("/api/test");
    }


    private Long validateTokenAndExtractUserId(String token) {
        WebClient webClient = WebClient.builder()
                .baseUrl(kakaoUserInfoUrl)
                .build();

        KakaoTokenInfoResponseDto kakaoTokenInfoResponseDto = webClient
                .get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        Mono.error(new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN_OR_SOCIAL_ID, ErrorCode.INVALID_TOKEN_OR_SOCIAL_ID.getMsg()))
                )
                .onStatus(status -> status.is5xxServerError(), response ->
                        Mono.error(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMsg()))
                )
                .bodyToMono(KakaoTokenInfoResponseDto.class)
                .block();

        if (kakaoTokenInfoResponseDto == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN_OR_SOCIAL_ID, ErrorCode.INVALID_TOKEN_OR_SOCIAL_ID.getMsg());
        }
        log.info("{}", kakaoTokenInfoResponseDto.getExpires_in());
        return kakaoTokenInfoResponseDto.getId();
    }
}

