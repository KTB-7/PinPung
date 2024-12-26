package com.ktb7.pinpung.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb7.pinpung.dto.AI.*;
import com.ktb7.pinpung.dto.Place.SimplePlaceDto;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.PlaceRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import com.ktb7.pinpung.repository.TagRepository;
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
import java.util.Optional;

import static com.ktb7.pinpung.exception.common.ErrorCode.RECOMMEND_TAGS_REQUEST_FAILED;

@Service
@Slf4j
public class AiService {

    private final WebClient webClient;
    private final PlaceRepository placeRepository;
    private final PlaceService placeService;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;

    public AiService(WebClient.Builder webClientBuilder, @Value("${fastapi.server.url}") String fastApiUrl, PlaceRepository placeRepository, PlaceService placeService, TagRepository tagRepository, ReviewRepository reviewRepository) {
        this.webClient = webClientBuilder.baseUrl(fastApiUrl).build();
        this.placeRepository = placeRepository;
        this.placeService = placeService;
        this.tagRepository = tagRepository;
        this.reviewRepository = reviewRepository;
    }

    public Boolean genTags(Long placeId, String reviewText, String reviewImageUrl, Long userId) {
        log.info("start generating tags");
        log.info("Request Data: placeId={}, reviewText={}, reviewImageUrl={}, userId={}", placeId, reviewText, reviewImageUrl, userId);

        GenerateTagsAIResponseDto response;
        try {
            String rawResponse = webClient.post()
                    .uri("/gen_tags/")
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(buildRequestBody(placeId, reviewText, reviewImageUrl, userId)), String.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Raw response from AI server: {}", rawResponse);

            // JSON 파싱, 매핑..
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(rawResponse, GenerateTagsAIResponseDto.class);

        } catch (Exception e) {
            log.error("Failed to call AI server", e);
            response = new GenerateTagsAIResponseDto(false); // 기본값 반환
        }

        Boolean isGened = response != null && response.getIsGened() != null ? response.getIsGened() : false;
        log.info("genTags 응답 isGened: {}", isGened);
        return isGened;
    }

    private String buildRequestBody(Long placeId, String reviewText, String reviewImageUrl, Long userId) {
        return String.format("{\"place_id\": %d, \"review_text\": \"%s\", \"review_image_url\": \"%s\", \"user_id\":%d}",
                placeId, reviewText, reviewImageUrl, userId);
    }

    public RecommendTagsAIResponseDto recommend(Long userId, List<Long> placeIdList) {

        try {
            return webClient.post()
                    .uri("/get_recs/ai")
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

    public TrendingTagsAIResponseDto getTrending(Long userId, List<Long> placeIdList) {

        try {
            return webClient.post()
                    .uri("/get_recs/popular")
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(buildRecommendRequestBody(userId, placeIdList)), String.class)
                    .retrieve()
                    .bodyToMono(TrendingTagsAIResponseDto.class)
                    .block();

        } catch (Exception e) {
            log.error("추천 태그 요청 중 오류 발생", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, RECOMMEND_TAGS_REQUEST_FAILED);
        }
    }

    private String buildRecommendRequestBody(Long userId, List<Long> placeIdList) {
        return String.format("{\"user_id\": %d, \"place_ids\": %s}",
                userId, placeIdList.toString());
    }

    public TrendingTagsResponseDto changeFormat2Trending(Long userId, TrendingTagsAIResponseDto trendingTagsAIResponse) {

        List<String> hashtags = trendingTagsAIResponse.getHashtags();
        List<List<Long>> cafeList = trendingTagsAIResponse.getCafe_list();

        // PlacesPerTagDto 리스트 생성
        List<PlacesPerTagDto> placesPerTags = new ArrayList<>();
        for (int i = 0; i < hashtags.size(); i++) {
            String tagName = hashtags.get(i);
            List<Long> placeIdList = cafeList.get(i);

            List<SimplePlaceDto> places = changeFormat2Recommend(new RecommendTagsAIResponseDto(placeIdList));

            placesPerTags.add(new PlacesPerTagDto(tagName, places.size(), places));
        }

        return new TrendingTagsResponseDto(placesPerTags.size(), placesPerTags);
    }

    public List<SimplePlaceDto> changeFormat2Recommend(RecommendTagsAIResponseDto recommendTagsAIResponse) {

        List<SimplePlaceDto> places = new ArrayList<>();
        for (int i = 0; i < recommendTagsAIResponse.getCafe_list().size(); i++) {
            Long placeId = recommendTagsAIResponse.getCafe_list().get(i);
            // placeId를 이용해 Place 엔티티 조회 (없을 경우 예외 처리)
            Place place = placeRepository.findById(placeId)
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND));

            // placeId로 가장 최근 리뷰 조회 (리뷰가 없는 경우 Optional.empty())
            Optional<Review> recentReviewOpt = reviewRepository.findTopByPlaceIdOrderByCreatedAtDesc(placeId);

            // 가장 최근 리뷰에서 이미지가 있을 경우 imageId를 가져오고, 없으면 null 설정
            Long recentImageId = recentReviewOpt
                    .map(Review::getImageId)
                    .orElse(null);

            // placeId로 관련 태그 목록 조회
            List<String> tagNames = tagRepository.findTagNamesByPlaceId(placeId);

            // SimplePlaceDto 객체 생성 및 추가
            places.add(new SimplePlaceDto(placeId, place.getPlaceName(), place.getAddress(), tagNames, recentImageId,
                    place.getX(), place.getY()));
        }

        return places;
    }
}
