package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.SearchResponseDto;
import com.ktb7.pinpung.repository.SearchRepository;
import com.ktb7.pinpung.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {

    private final SearchRepository searchRepository;
    private final TagRepository tagRepository;

    public SearchService(SearchRepository searchRepository, TagRepository tagRepository) {
        this.searchRepository = searchRepository;
        this.tagRepository = tagRepository;
    }

    /*
   GET search/places
   place id 리스트를 받아 장소별 리뷰 개수, 태그 조회
   */
    public List<SearchResponseDto> getPlacesWithReviewCountsAndTags(List<Long> placeIds) {
        // 리뷰 개수 조회
        List<Object[]> reviewCounts = searchRepository.findReviewCountsByPlaceIds(placeIds);

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

