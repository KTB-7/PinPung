package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.Place.SimplePlaceDto;
import com.ktb7.pinpung.entity.Place;
import com.ktb7.pinpung.entity.Review;
import com.ktb7.pinpung.exception.common.CustomException;
import com.ktb7.pinpung.exception.common.ErrorCode;
import com.ktb7.pinpung.repository.PlaceRepository;
import com.ktb7.pinpung.repository.PlaceTagRepository;
import com.ktb7.pinpung.repository.ReviewRepository;
import com.ktb7.pinpung.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService {

    private final PlaceTagRepository placeTagRepository;
    private final TagRepository tagRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    public List<String> getTags() {
        // 전체 placeTag 테이블에서 tagCount가 가장 높은 태그 5개 가져오기
        List<String> topTags = placeTagRepository.findTop5TagsByTagCount();

        if (topTags.size() > 5) {
            topTags = topTags.subList(0, 5);
        }

        log.info("Top 5 tags: {}", topTags);
        return topTags;
    }

    public List<SimplePlaceDto> getPlacesFromTag(String tagName, List<Long> placeIdList) {
        // 1. tagName으로 tag 테이블에서 tagId를 찾고, PlaceTag와 조인해서 placeId를 가져오기
        List<Long> foundPlaceIds = placeTagRepository.findPlaceIdsByTagName(tagName);

        // 2. 찾은 placeId가 placeIdList에 있는지 확인하고, SimplePlaceDto로 변환
        List<SimplePlaceDto> responseList = new ArrayList<>();
        for (Long placeId : foundPlaceIds) {
            if (placeIdList.contains(placeId)) {
                // Place 엔티티 조회
                Place place = placeRepository.findById(placeId)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, ErrorCode.PLACE_NOT_FOUND));

                // 해당 Place의 태그 목록 조회
                List<String> tags = tagRepository.findTagNamesByPlaceId(placeId);

                // placeId로 가장 최근 리뷰 조회 (리뷰가 없는 경우 Optional.empty())
                Optional<Review> recentReviewOpt = reviewRepository.findTopByPlaceIdOrderByCreatedAtDesc(placeId);

                // 가장 최근 리뷰에서 이미지가 있을 경우 imageId를 가져오고, 없으면 null 설정
                Long recentImageId = recentReviewOpt
                        .map(Review::getImageId)
                        .orElse(null);

                // SimplePlaceDto 생성 및 응답 리스트에 추가
                responseList.add(new SimplePlaceDto(
                        place.getPlaceId(),
                        place.getPlaceName(),
                        place.getAddress(),
                        tags,
                        recentImageId,
                        place.getX(),
                        place.getY()
                ));
            }
        }
        return responseList;
    }

}
