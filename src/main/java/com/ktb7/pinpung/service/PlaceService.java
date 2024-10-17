package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.PlaceInfoResponseDto;
import com.ktb7.pinpung.dto.PlaceNearbyResponseDto;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.repository.PlaceRepository;
import com.ktb7.pinpung.repository.PungRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import com.ktb7.pinpung.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.Clock;
import java.util.Collections;

@Service
@Slf4j
public class PlaceService {

    private final PungRepository pungRepository;
    private final Clock clock;
    private final PlaceRepository placeRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;

    public PlaceService(PungRepository pungRepository, Clock clock, PlaceRepository placeRepository, TagRepository tagRepository, ReviewRepository reviewRepository) {
        this.pungRepository = pungRepository;
        this.clock = clock;
        this.placeRepository = placeRepository;
        this.tagRepository = tagRepository;
        this.reviewRepository = reviewRepository;
    }

    /*
    GET places/nearby
    place id 리스트를 받아 24시간 내 업로드된 펑이 있는 장소의 id와 대표 펑 이미지 반환
    */
    public List<PlaceNearbyResponseDto> getPlacesWithRepresentativeImage(List<String> placeIds) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        return placeIds.stream().map(placeId -> {
            // PungRepository에서 24시간 내의 이미지 URL을 가져옴. 없으면 null 반환
            String imageUrl = pungRepository.findLatestByPlaceIdWithin24Hours(placeId, yesterday)
                    .map(Pung::getImageUrl)
                    .orElse(null);

            log.info("places/nearby imageUrl: {}", imageUrl);
            return new PlaceNearbyResponseDto(placeId, imageUrl);
        }).collect(Collectors.toList());
    }

    /*
    GET places/{placeId}
    place id를 받아 해당 장소의 정보, 리뷰, 대표 펑 반환
    */
    public PlaceInfoResponseDto getPlaceInfo(String placeId) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        // place info 조회
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid placeId: " + placeId));

        // tags 조회
        List<Object[]> tagObjects = tagRepository.findTagsByPlaceIds(Collections.singletonList(placeId));

        // Object[]에서 tagName만 추출
        List<String> tags = tagObjects.stream()
                .map(tagObj -> (String) tagObj[1]) // 두 번째 요소(tagName)를 가져옴
                .collect(Collectors.toList());

        // representative pung 조회
        Optional<Pung> representativePung = pungRepository.findLatestByPlaceIdWithin24Hours(placeId, yesterday);
        log.info("representativePung: {}", representativePung);

        // reviews 조회
        List<Review> reviews = reviewRepository.findByPlaceId(placeId);

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
}
