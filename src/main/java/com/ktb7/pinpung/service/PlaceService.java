package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.*;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.repository.PlaceRepository;
import com.ktb7.pinpung.repository.PungRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import com.ktb7.pinpung.repository.TagRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Clock;

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

    @Value("${kakao.client_id}")
    private String client_id;

    public List<Long> categorySearch(String x, String y, Integer radius) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + client_id);
        String requestUrl = KAKAO_LOCAL_API_URL + "?category_group_code=CE7&x=" + x + "&y=" + y + "&radius=" + radius;

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);

        List<Long> placeIds = new ArrayList<>();

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");

            for (Map<String, Object> document : documents) {
                String kakaoPlaceId = (String) document.get("id");

                // 이미 존재하는지 확인
                Optional<Place> existingPlace = placeRepository.findByKakaoPlaceId(kakaoPlaceId);

                if (existingPlace.isPresent()) {
                    // 이미 존재하는 경우, 저장하지 않고 기존 ID 추가
                    placeIds.add(existingPlace.get().getPlaceId());
                } else {
                    // 존재하지 않는 경우 새로 저장
                    String placeName = (String) document.get("place_name");
                    String address = (String) document.get("road_address_name");
                    String longitude = (String) document.get("x");
                    String latitude = (String) document.get("y");

                    Place place = new Place();
                    place.setKakaoPlaceId(kakaoPlaceId);
                    place.setPlaceName(placeName);
                    place.setAddress(address);
                    place.setX(longitude);
                    place.setY(latitude);

                    Place savedPlace = placeRepository.save(place);
                    placeIds.add(savedPlace.getPlaceId());
                }
            }
        } else {
            log.error("카테고리 검색 API 호출 실패: {}", response.getStatusCode());
        }
        return placeIds;
    }


    /*
    GET places/nearby
    place id 리스트를 받아 24시간 내 업로드된 펑이 있는 장소의 id와 대표 펑 이미지 반환
    */
    public List<PlaceNearbyDto> getPlacesWithRepresentativeImage(List<Long> placeIds) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        return placeIds.stream().map(placeId -> {
            // PungRepository에서 24시간 내의 이미지 URL을 가져옴. 없으면 null 반환
            Long imageWithText = pungRepository.findFirstByPlaceIdAndCreatedAtAfterOrderByCreatedAtDesc(placeId, yesterday)
                    .map(Pung::getImageWithText)
                    .orElse(null);
            boolean hasPung = imageWithText != null;

            Place place = placeRepository.findById(placeId).orElse(null);
            if (place == null) {
                log.error("Place not found for placeId: {}", placeId);
                return null;
            }

            log.info("places/nearby placeId imageUrl: {} {}", placeId, imageWithText);
            return new PlaceNearbyDto(
                    placeId,
                    hasPung,
                    imageWithText,
                    place.getX(),
                    place.getY()
            );
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    /*
    GET places/{placeId}
    place id를 받아 해당 장소의 정보, 리뷰, 대표 펑 반환
    */
    public PlaceInfoResponseDto getPlaceInfo(Long placeId) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        // place info 조회
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid placeId: " + placeId));
        log.info("places/{placeId} placeId placeInfo: {} {}", placeId, place);

        // tags 조회
        List<Object[]> tagObjects = tagRepository.findTagsByPlaceIds(Collections.singletonList(placeId));

        // Object[]에서 tagName만 추출
        List<String> tags = tagObjects.stream()
                .map(tagObj -> (String) tagObj[1]) // 두 번째 요소(tagName)를 가져옴
                .collect(Collectors.toList());
        log.info("places/{placeId} tags {}", tags);

        // representative pung 조회
        Optional<Pung> representativePung = pungRepository.findFirstByPlaceIdAndCreatedAtAfterOrderByCreatedAtDesc(placeId, yesterday);
        log.info("places/{placeId} representativePung: {}", representativePung);

        // reviews 조회
        List<Review> reviewList = reviewRepository.findByPlaceId(placeId);
        log.info("places/{placeId} reviews {}", reviewList);
        ReviewsDto reviews = new ReviewsDto(reviewList.size(), reviewList);

        // PlaceInfoResponseDto로 반환
        return new PlaceInfoResponseDto(
                place.getPlaceId(),
                place.getPlaceName(),
                place.getAddress(),
                tags,
                reviews,
                representativePung.orElse(null)  // 대표 펑이 없는 경우 null
        );
    }

    /*
   GET search/places
   place id 리스트를 받아 장소별 리뷰 개수, 태그 조회
   */
    public List<SearchResponseDto> getPlacesWithReviewCountsAndTags(List<Long> placeIds) {
        // 리뷰 개수 조회
//        List<Object[]> reviewCounts = searchRepository.findReviewCountsByPlaceIds(placeIds);
        List<Object[]> reviewCounts = reviewRepository.findReviewCountsByPlaceIds(placeIds);

        // 태그 조회
        List<Object[]> tags = tagRepository.findTagsByPlaceIds(placeIds);

        // 리뷰 개수 맵으로 변환
        Map<Long, Long> reviewCountMap = reviewCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // placeId
                        row -> (Long) row[1]     // reviewCount
                ));
        log.info("/search/places review counts: {}", reviewCountMap);

        // 태그 맵으로 변환
        Map<Long, List<String>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],              // placeId
                        Collectors.mapping(row -> (String) row[1], Collectors.toList()) // tagName
                ));
        log.info("/search/tags tags: {}", tagMap);

        // 결과 생성
        return placeIds.stream().map(placeId -> {
            Long reviewCount = reviewCountMap.getOrDefault(placeId, 0L);
            List<String> tagList = tagMap.getOrDefault(placeId, Collections.emptyList());

            return new SearchResponseDto(placeId, tagList, reviewCount);
        }).collect(Collectors.toList());
    }
}
