package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.dto.Search.SearchPlaceInfoDto;
import com.ktb7.pinpung.dto.Search.SearchResponseDto;
import com.ktb7.pinpung.dto.Search.SearchTagReviewDto;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.PlaceRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import com.ktb7.pinpung.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final PlaceRepository placeRepository;
    @Value("${spring.ai.openai.api-key}")
    private String openaiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final WebClient webClient = WebClient.builder().build();

    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;

    public Boolean useGpt(String keyword) {
        // 프롬프트 생성
        String prompt = "다음 키워드에 지역에 대한 정보(ex. 강남, 강원도, 판교역, 판교 유스페이스몰 등 특정 위치에 대한 정보이어야 하며, 카페 이름은 위치 정보가 아님)가 있으면 '1'만을, 없으면 '0'만을 응답하세요: \"" + keyword + "\"";

        // 요청 바디 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        try {
            // OpenAI API 호출
            Map<String, Object> response = webClient.post()
                    .uri(OPENAI_API_URL)
                    .header("Authorization", "Bearer " + openaiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()

                    //ParameterizedTypeReference는 복잡한 제네릭 타입을 처리하기 위해 사용
                    //HTTP 응답 본문이 Map<String, Object> 구조라고 Spring에게 알려줌
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            // 응답에서 "choices" 가져오기
            if (response == null || !response.containsKey("choices")) {
                log.error("GPT 응답에서 'choices'가 누락되었습니다.");
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "GPT 응답에 'choices'가 없습니다.");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                log.error("GPT 응답에서 선택지가 없습니다.");
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "GPT 응답에 선택지가 없습니다.");
            }

            // 첫 번째 선택지에서 "message" 가져오기
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null || !message.containsKey("content")) {
                log.error("GPT 응답에서 'message' 또는 'content'가 누락되었습니다.");
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "GPT 응답에 'message' 또는 'content'가 없습니다.");
            }

            String content = ((String) message.get("content")).trim();

            log.info("content: {}", content);

            // 결과 처리
            if ("1".equals(content)) {
                return true;
            } else if ("0".equals(content)) {
                return false;
            } else {
                log.warn("예상치 못한 GPT 응답: {}", content);
                return false;
            }

        } catch (CustomException e) {
            log.error("OpenAI API 호출 중 알 수 없는 오류 발생", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNEXPECTED_ERROR, "OpenAI API 호출 중 오류가 발생했습니다.");
        }
    }

    public List<SearchTagReviewDto> getPlacesWithReviewCountsAndTags(List<Long> placeIds) {
        List<Object[]> reviewCounts = reviewRepository.findReviewCountsByPlaceIds(placeIds);
        List<Object[]> tags = tagRepository.findTagsByPlaceIds(placeIds);

        Map<Long, Long> reviewCountMap = reviewCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
        log.info("/tag-reviews review counts: {}", reviewCountMap);

        Map<Long, List<String>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));
        log.info("/tag-reviews tags: {}", tagMap);

        return placeIds.stream().map(placeId -> {
            Long reviewCount = reviewCountMap.getOrDefault(placeId, 0L);
            List<String> tagList = tagMap.getOrDefault(placeId, Collections.emptyList());

            return new SearchTagReviewDto(placeId, tagList, reviewCount);
        }).collect(Collectors.toList());
    }

    public SearchResponseDto makeResponse(Long userId, List<PlaceNearbyDto> placeNearbyInfoList, List<SearchTagReviewDto> placeNearbyTagReviewList, String sortType) {
        // 두개를 placeId 기준으로 합쳐서 search place info 채우기
        List<SearchPlaceInfoDto> searchPlaceInfoList = placeNearbyInfoList.stream()
                .map(place -> {
                    // 태그 및 리뷰 정보를 찾아서 병합
                    SearchTagReviewDto tagReviewDto = placeNearbyTagReviewList.stream()
                            .filter(tagReview -> tagReview.getPlaceId().equals(place.getPlaceId()))
                            .findFirst()
                            .orElse(null);

                    String address = placeRepository.findAddressByPlaceId(place.getPlaceId());

                    // SearchPlaceInfoDto 생성
                    return new SearchPlaceInfoDto(
                            place.getPlaceId(),
                            place.getPlaceName(),
                            address,
                            place.getHasPung(),
                            place.getByFriend(),
                            place.getImageId(),
                            tagReviewDto != null ? tagReviewDto.getTags() : List.of(),
                            tagReviewDto != null ? tagReviewDto.getReviewCount() : 0L,
                            place.getX(),
                            place.getY()
                    );
                })
                .toList();

        // SearchResponseDto 생성
        SearchResponseDto response = new SearchResponseDto(
                userId,
                sortType,
                (long) searchPlaceInfoList.size(),
                searchPlaceInfoList
        );

        return response;
    }

}