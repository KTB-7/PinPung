package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.GenerateTagsRequestDto;
import com.ktb7.pinpung.dto.GenerateTagsResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AiService {

    private final WebClient webClient;

    public AiService(WebClient.Builder webClientBuilder, @Value("${fastapi.server.url}") String fastApiUrl) {
        this.webClient = webClientBuilder.baseUrl(fastApiUrl).build(); // FastAPI 서버 URL 설정
    }

    public Boolean genTags(Long placeId, String reviewText, String reviewImageUrl) {
        log.info("start generating tags");
        log.info("{}{}{}", placeId, reviewText, reviewImageUrl);

        GenerateTagsResponseDto response = webClient.post()
                .uri("/gen_tags/")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(buildRequestBody(placeId, reviewText, reviewImageUrl)), String.class)
                .retrieve()
                .bodyToMono(GenerateTagsResponseDto.class)
                .block();

        Boolean isGened = response != null ? response.getIsGened() : false;
        log.info("genTags 응답 isGened: {}", isGened);
        return isGened;
    }

    private String buildRequestBody(Long placeId, String reviewText, String reviewImageUrl) {
        // JSON 형식으로 직접 문자열을 작성하여 전송
        return String.format("{\"place_id\": %d, \"review_text\": \"%s\", \"review_image_url\": \"%s\"}",
                placeId, reviewText, reviewImageUrl);
    }
}
