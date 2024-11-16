package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.Place.PlaceInfoResponseDto;
import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.dto.Place.SearchResponseDto;
import com.ktb7.pinpung.dto.Review.ReviewsDto;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.PlaceRepository;
import com.ktb7.pinpung.repository.PungRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import com.ktb7.pinpung.repository.TagRepository;
import com.ktb7.pinpung.util.RepositoryHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;import reactor.core.publisher.Mono;


@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceService {

    private final PungRepository pungRepository;
    private final Clock clock;
    private final PlaceRepository placeRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;

    private static final String KAKAO_LOCAL_API_URL = "https://dapi.kakao.com/v2/local/search/category.json";
    private final S3Service s3Service;
    private final RepositoryHelper repositoryHelper;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    private final WebClient webClient = WebClient.builder().build();

    public List<Long> categorySearch(String x, String y, Integer radius) {
        List<Long> placeIds = new ArrayList<>();
        int page = 1;
        int size = 15;
        int maxPage = 3;

        while (page <= maxPage) {
            String requestUrl = KAKAO_LOCAL_API_URL + "?category_group_code=CE7&x=" + x + "&y=" + y +
                    "&radius=" + radius + "&page=" + page + "&size=" + size;

            Map<String, Object> response = webClient.get()
                    .uri(requestUrl)
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + clientId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                        log.error("카테고리 검색 API 호출 실패: {}", clientResponse.statusCode());
                        return Mono.error(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "카테고리 검색 API 호출 실패"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            // block : 비동기 작업인 Mono를 동기적으로 기다려 최종 결과를 반환

            List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
            if (documents == null) {
                log.error("카테고리 검색 API 응답에 문서가 없습니다.");
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "카테고리 검색 API 응답에 문서가 없습니다.");
            }

            int documentCount = documents.size();

            for (Map<String, Object> document : documents) {
                String kakaoPlaceId = (String) document.get("id");

                Optional<Place> existingPlace = placeRepository.findByKakaoPlaceId(kakaoPlaceId);
                if (existingPlace.isPresent()) {
                    placeIds.add(existingPlace.get().getPlaceId());
                } else {
                    Place place = new Place();
                    place.setKakaoPlaceId(kakaoPlaceId);
                    place.setPlaceName((String) document.get("place_name"));
                    place.setAddress((String) document.get("road_address_name"));
                    place.setX((String) document.get("x"));
                    place.setY((String) document.get("y"));

                    Place savedPlace = placeRepository.save(place);
                    placeIds.add(savedPlace.getPlaceId());
                }
            }
            if (documentCount < size) {
                break;
            }
            page++;
        }
        return placeIds;
    }



    public List<PlaceNearbyDto> getPlacesWithRepresentativeImage(List<Long> placeIds) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        return placeIds.stream().map(placeId -> {
            Long imageWithText = pungRepository.findFirstByPlaceIdAndCreatedAtAfterOrderByCreatedAtDesc(placeId, yesterday)
                    .map(Pung::getImageId)
                    .orElse(null);
            boolean hasPung = imageWithText != null;

            Place place = repositoryHelper.findPlaceById(placeId);

            return new PlaceNearbyDto(
                    placeId,
                    place.getPlaceName(),
                    hasPung,
                    imageWithText,
                    place.getX(),
                    place.getY()
            );
        }).collect(Collectors.toList());
    }

    public PlaceInfoResponseDto getPlaceInfo(Long placeId) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        Place place = repositoryHelper.findPlaceById(placeId);
        log.info("places/{placeId} placeId placeInfo: {} {}", placeId, place);

        List<Object[]> tagObjects = Optional.ofNullable(tagRepository.findTagsByPlaceIds(List.of(placeId)))
                .orElse(Collections.emptyList());
        List<String> tags = tagObjects.stream()
                .map(tagObj -> (String) tagObj[1])
                .collect(Collectors.toList());

        log.info("tags {}:", tags);

        // 대표 펑 & 이미지 ID 조회
        Optional<Pung> representativePung = pungRepository.findFirstByPlaceIdAndCreatedAtAfterOrderByCreatedAtDesc(placeId, yesterday);
        Long imageId = null;
        if (representativePung.isPresent()) {
            imageId = representativePung.get().getImageId();
            String objectKey = "uploaded-images/" + imageId;
            if (!s3Service.doesObjectExist(objectKey)) {
                log.error("이미지 ID {}에 대한 S3 객체를 찾을 수 없습니다.", imageId);
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, "이미지를 찾을 수 없습니다.");
            }
        }

        // 리뷰 조회
        List<Review> reviewList = reviewRepository.findByPlaceId(placeId);
        ReviewsDto reviews = new ReviewsDto(reviewList.size(), reviewList);

        return new PlaceInfoResponseDto(
                place.getPlaceId(),
                place.getPlaceName(),
                place.getAddress(),
                tags,
                reviews,
                representativePung.orElse(null)
        );
    }

    public List<SearchResponseDto> getPlacesWithReviewCountsAndTags(List<Long> placeIds) {
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

            return new SearchResponseDto(placeId, tagList, reviewCount);
        }).collect(Collectors.toList());
    }
}