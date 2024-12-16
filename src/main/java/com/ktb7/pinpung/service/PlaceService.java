package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.Place.PlaceInfoResponseDto;
import com.ktb7.pinpung.dto.Place.PlaceNearbyDto;
import com.ktb7.pinpung.dto.Pung.PungDto;
import com.ktb7.pinpung.dto.Review.ReviewDto;
import com.ktb7.pinpung.dto.Review.ReviewsDto;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.entity.User;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.*;
import com.ktb7.pinpung.util.RepositoryHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    private static final String KAKAO_LOCAL_API_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private final S3Service s3Service;
    private final RepositoryHelper repositoryHelper;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    private final WebClient webClient = WebClient.builder().build();

    public List<Long> categorySearch(String keyword, String swLng, String swLat, String neLng, String neLat, String x, String y, String sort) {
        List<Long> placeIds = new ArrayList<>();
        int page = 1;
        int size = 15;
        int maxPage = 3;

        while (page <= maxPage) {
            StringBuilder requestUrl = new StringBuilder(KAKAO_LOCAL_API_URL)
                    .append("?query=").append(keyword)
                    .append("&category_group_code=CE7")
                    .append("&page=").append(page)
                    .append("&size=").append(size);

            if (swLng != null && swLat != null && neLng != null && neLat != null) {
                requestUrl.append("&rect=").append(swLng).append(",").append(swLat).append(",").append(neLng).append(",").append(neLat);
            }

            if (x != null && y != null) {
                requestUrl.append("&x=").append(x).append("&y=").append(y).append("&sort=").append(sort);
            }

            Map<String, Object> response = webClient.get()
                    .uri(requestUrl.toString())
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + clientId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                        log.error("카테고리 검색 API 호출 실패: {}", clientResponse.statusCode());
                        return Mono.error(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "카테고리 검색 API 호출 실패"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
            if (documents == null) {
                log.error("카테고리 검색 API 응답에 문서가 없습니다.");
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.API_CALL_FAILED, "카테고리 검색 API 응답에 문서가 없습니다.");
            }

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
            if (documents.size() < size) {
                break;
            }
            page++;
        }
        return placeIds;
    }


    public List<PlaceNearbyDto> getPlacesWithRepresentativeImage(Long userId, List<Long> placeIds) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        // 팔로워 리스트를 한 번만 가져옴
        List<Long> followerList = followRepository.findFollowersByUserId(userId)
                .stream()
                .map(User::getUserId)
                .toList();

        return placeIds.stream().map(placeId -> {
            boolean byFriend = false;
            boolean hasPung = false;
            Long imageId = null;

            Pung pung = pungRepository.findFirstByPlaceIdAndIsReviewFalse(placeId)
                    .orElse(null);

            if (pung != null) {
                imageId = pung.getImageId();
                hasPung = imageId != null;

                // 팔로워 확인
                Long authorId = pung.getUserId();
                if (followerList.contains(authorId)) {
                    byFriend = true;
                }
            }

            Place place = repositoryHelper.findPlaceById(placeId);

            return new PlaceNearbyDto(
                    placeId,
                    place.getPlaceName(),
                    hasPung,
                    byFriend,
                    imageId,
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
        Optional<Pung> representativePung = pungRepository.findFirstByPlaceIdAndIsReviewFalse(placeId);

        PungDto pungDto = null;
        if (representativePung.isPresent()) {
            Pung pung = representativePung.get();
            Long imageId = pung.getImageId();
            String objectKey = "uploaded-images/" + imageId;

            if (!s3Service.doesObjectExist(objectKey)) {
                log.error("이미지 ID {}에 대한 S3 객체를 찾을 수 없습니다.", imageId);
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, "이미지를 찾을 수 없습니다.");
            }

            // userId로 userName 조회
            String userName = userRepository.findById(pung.getUserId())
                    .map(User::getUserName)
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

            pungDto = new PungDto();
            pungDto.setPungId(pung.getPungId());
            pungDto.setUserId(pung.getUserId());
            pungDto.setUserName(userName); // userName 추가
            pungDto.setImageId(pung.getImageId());
            pungDto.setText(pung.getText());
            pungDto.setCreatedAt(pung.getCreatedAt());
            pungDto.setUpdatedAt(pung.getUpdatedAt());
        }

        // 리뷰 조회
        List<Review> reviewList = reviewRepository.findByPlaceId(placeId);

        // Review -> ReviewDto 변환
        List<ReviewDto> reviewDtoList = reviewList.stream()
                .map(review -> {
                    User user = userRepository.findById(review.getUserId())
                            .orElseThrow(() -> new CustomException(
                                    HttpStatus.NOT_FOUND,
                                    ErrorCode.USER_NOT_FOUND,
                                    "유저를 찾을 수 없습니다. ID: " + review.getUserId()
                            ));
                    return new ReviewDto(
                            review.getReviewId(),
                            review.getUserId(),
                            user.getUserName(),
                            review.getImageId(),
                            review.getText(),
                            review.getCreatedAt(),
                            review.getUpdatedAt()
                    );
                }).toList();

        ReviewsDto reviews = new ReviewsDto(reviewDtoList.size(), reviewDtoList);

        return new PlaceInfoResponseDto(
                place.getPlaceId(),
                place.getPlaceName(),
                place.getAddress(),
                tags,
                reviews,
                pungDto
        );
    }
}