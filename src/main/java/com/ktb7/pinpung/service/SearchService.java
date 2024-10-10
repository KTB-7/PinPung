package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.SearchResponseDto;
import com.ktb7.pinpung.repository.SearchRepository;
import com.ktb7.pinpung.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final SearchRepository searchRepository;
    private final TagRepository tagRepository;

    public SearchService(SearchRepository searchRepository, TagRepository tagRepository) {
        this.searchRepository = searchRepository;
        this.tagRepository = tagRepository;
    }

    public List<SearchResponseDto> getPlacesWithReviewCountsAndTags(List<String> placeIds) {
        // 리뷰 개수 조회
        List<Object[]> reviewCounts = searchRepository.findReviewCountsByPlaceIds(placeIds);

        // 태그 조회
        List<Object[]> tags = tagRepository.findTagsByPlaceIds(placeIds);

        // 리뷰 개수 맵으로 변환
        Map<String, Long> reviewCountMap = reviewCounts.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],  // placeId
                        row -> (Long) row[1]      // reviewCount
                ));

        // 태그 맵으로 변환
        Map<String, List<String>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(
                        row -> (String) row[0],              // placeId
                        Collectors.mapping(row -> (String) row[1], Collectors.toList()) // tagName
                ));

        // 결과 생성
        return placeIds.stream().map(placeId -> {
            Long reviewCount = reviewCountMap.getOrDefault(placeId, 0L);
            List<String> tagList = tagMap.getOrDefault(placeId, Collections.emptyList());

            return new SearchResponseDto(placeId, tagList, reviewCount);
        }).collect(Collectors.toList());
    }
}

