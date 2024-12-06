package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.AI.*;
import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ktb7.pinpung.exception.common.ErrorCode.RECOMMEND_TAGS_REQUEST_FAILED;

@Service
@Slf4j
public class AiService {

    private final WebClient webClient;
    private final PlaceRepository placeRepository;
    private final PlaceService placeService;

    public AiService(WebClient.Builder webClientBuilder, @Value("${fastapi.server.url}") String fastApiUrl, PlaceRepository placeRepository, PlaceService placeService) {
        this.webClient = webClientBuilder.baseUrl(fastApiUrl).build();
        this.placeRepository = placeRepository;
        this.placeService = placeService;
    }

    public Boolean genTags(Long placeId, String reviewText, String reviewImageUrl, Long userId) {
        log.info("start generating tags");
        log.info("{}{}{}", placeId, reviewText, reviewImageUrl, userId);

        GenerateTagsAIResponseDto response = webClient.post()
                .uri("/gen_tags/")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(buildRequestBody(placeId, reviewText, reviewImageUrl, userId)), String.class)
                .retrieve()
                .bodyToMono(GenerateTagsAIResponseDto.class)
                .block();

        Boolean isGened = response != null ? response.getIsGened() : false;
        log.info("genTags 응답 isGened: {}", isGened);
        return isGened;
    }

    private String buildRequestBody(Long placeId, String reviewText, String reviewImageUrl, Long userId) {
        return String.format("{\"place_id\": %d, \"review_text\": \"%s\", \"review_image_url\": \"%s\"}",
                placeId, reviewText, reviewImageUrl);
    }

    public RecommendTagsAIResponseDto recommend(Long userId, List<Long> placeIdList) {

        try {
            return webClient.post()
                    .uri("/get_recs/ai/")
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(buildRecommendRequestBody(userId, placeIdList)), String.class)
                    .retrieve()
                    .bodyToMono(RecommendTagsAIResponseDto.class)
                    .block();

        } catch (Exception e) {
            log.error("추천 태그 요청 중 오류 발생", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, RECOMMEND_TAGS_REQUEST_FAILED);
        }
    }

    public RecommendTagsAIResponseDto getTrending(Long userId, List<Long> placeIdList) {

        try {
            return webClient.post()
                    .uri("/get_recs/popular")
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(buildRecommendRequestBody(userId, placeIdList)), String.class)
                    .retrieve()
                    .bodyToMono(RecommendTagsAIResponseDto.class)
                    .block();

        } catch (Exception e) {
            log.error("추천 태그 요청 중 오류 발생", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, RECOMMEND_TAGS_REQUEST_FAILED);
        }
    }

    private String buildRecommendRequestBody(Long userId, List<Long> placeIdList) {
        return String.format("{\"userId\": %d, \"placeIdList\": %s}",
                userId, placeIdList.toString());
    }

    public RecommendTagsResponseDto changeFormat(Long userId, RecommendTagsAIResponseDto recommendTagsAIResponse) {

        List<String> hashtags = recommendTagsAIResponse.getHashtags();
        List<List<String>> cafeList = recommendTagsAIResponse.getCafe_list();

        // PlacesPerTagDto 리스트 생성
        List<PlacesPerTagDto> placesPerTags = new ArrayList<>();
        for (int i = 0; i < hashtags.size(); i++) {
            String tagName = hashtags.get(i);
            List<String> placeIdList = cafeList.get(i);

            List<Long> placeIdLongList = placeIdList.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<PlaceNearbyDto> places = placeService.getPlacesWithRepresentativeImage(userId, placeIdLongList);

            placesPerTags.add(new PlacesPerTagDto(tagName, places.size(), places));
        }

        return new RecommendTagsResponseDto(placesPerTags.size(), placesPerTags);
    }
}
