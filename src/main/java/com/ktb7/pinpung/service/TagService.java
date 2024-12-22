package com.ktb7.pinpung.service;

import com.ktb7.pinpung.repository.PlaceTagRepository;
import com.ktb7.pinpung.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService {

    private final PlaceTagRepository placeTagRepository;

    public List<String> getTags() {
        // 전체 placeTag 테이블에서 tagCount가 가장 높은 태그 5개 가져오기
        List<String> topTags = placeTagRepository.findTop5TagsByTagCount();

        if (topTags.size() > 5) {
            topTags = topTags.subList(0, 5);
        }

        log.info("Top 5 tags: {}", topTags);
        return topTags;
    }
}
