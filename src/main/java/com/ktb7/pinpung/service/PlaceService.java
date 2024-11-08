package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.*;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.PlaceRepository;
import com.ktb7.pinpung.repository.PungRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import com.ktb7.pinpung.repository.TagRepository;
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
import java.util.stream.Collectors;

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
                        return clientResponse.createException();
                    })
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
            int documentCount = documents.size();  // 페이지당 결과 수 확인

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

            // 문서 수가 15개 미만일 경우 반복문 종료
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
                    .map(Pung::getImageWithText)
                    .orElse(null);
            boolean hasPung = imageWithText != null;

            Place place = placeRepository.findById(placeId)
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND));

//            log.info("places/nearby placeId imageUrl: {} {}", placeId, imageWithText);
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

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND));
        log.info("places/{placeId} placeId placeInfo: {} {}", placeId, place);

        List<Object[]> tagObjects = tagRepository.findTagsByPlaceIds(Collections.singletonList(placeId));
        List<String> tags = tagObjects.stream()
                .map(tagObj -> (String) tagObj[1])
                .collect(Collectors.toList());
//        log.info("places/{placeId} tags {}", tags);

        // 대표 펑 & 이미지 조회
        Optional<Pung> representativePung = pungRepository.findFirstByPlaceIdAndCreatedAtAfterOrderByCreatedAtDesc(placeId, yesterday);
        byte[] imageData = null;
        if (representativePung.isPresent()) {
            Long imageId = representativePung.get().getImageWithText();
            if (imageId != null) {
                imageData = s3Service.getImageFile("uploaded-image/" + imageId); // S3에서 이미지 데이터 가져오기
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
                representativePung.orElse(null),
                imageData
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
        log.info("/search/places review counts: {}", reviewCountMap);

        Map<Long, List<String>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));
        log.info("/search/tags tags: {}", tagMap);

        return placeIds.stream().map(placeId -> {
            Long reviewCount = reviewCountMap.getOrDefault(placeId, 0L);
            List<String> tagList = tagMap.getOrDefault(placeId, Collections.emptyList());

            return new SearchResponseDto(placeId, tagList, reviewCount);
        }).collect(Collectors.toList());
    }
}