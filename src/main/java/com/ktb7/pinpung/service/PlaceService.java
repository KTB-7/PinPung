package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.PlaceNearbyResponseDto;
import com.ktb7.pinpung.repository.PungRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Clock;

@Service
public class PlaceService {

    private final PungRepository pungRepository;
    private final Clock clock; // Clock 객체를 주입받음

    public PlaceService(PungRepository pungRepository, Clock clock) {
        this.pungRepository = pungRepository;
        this.clock = clock; // Clock 초기화
    }

    public List<PlaceNearbyResponseDto> getPlacesWithRepresentativeImage(List<String> placeIds) {
        // 현재 시간 대신 clock을 이용하여 시간을 고정
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        return placeIds.stream().map(placeId -> {
            // PungRepository에서 24시간 내의 이미지 URL을 가져옴. 없으면 null 반환
            String imageUrl = pungRepository.findLatestImageByPlaceIdWithin24Hours(placeId, yesterday)
                    .orElse(null);
            // DTO 객체 생성
            return new PlaceNearbyResponseDto(placeId, imageUrl);
        }).collect(Collectors.toList());
    }
}
