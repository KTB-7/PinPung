package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.PlaceNearbyResponseDto;
import com.ktb7.pinpung.repository.PungRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;

class PlaceServiceTest {

    @Mock
    private PungRepository pungRepository;

    @Mock
    private Clock clock;  // Clock 객체를 모킹

    @InjectMocks
    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용으로 고정된 시간을 설정
        LocalDateTime fixedDateTime = LocalDateTime.of(2024, 10, 10, 12, 0);
        Clock fixedClock = Clock.fixed(fixedDateTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        // Clock이 고정된 시간을 반환하도록 모킹
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    void testGetPlacesWithRepresentativeImage() {
        // 테스트용 장소 ID 목록
        List<String> placeIds = Arrays.asList("1abc", "2def", "3ghi");
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        // PungRepository 모킹
        when(pungRepository.findLatestImageByPlaceIdWithin24Hours("1abc", yesterday))
                .thenReturn(Optional.of("http://example.com/image1.jpg"));

        when(pungRepository.findLatestImageByPlaceIdWithin24Hours("2def", yesterday))
                .thenReturn(Optional.of("http://example.com/image2.jpg"));

        when(pungRepository.findLatestImageByPlaceIdWithin24Hours("3ghi", yesterday))
                .thenReturn(Optional.empty()); // 이미지가 없는 경우

        // 테스트 실행
        List<PlaceNearbyResponseDto> result = placeService.getPlacesWithRepresentativeImage(placeIds);

        // 결과 검증
        assertEquals(3, result.size());

        // 첫 번째 장소 검증
        assertEquals("1abc", result.get(0).getPlaceId());
        assertEquals("http://example.com/image1.jpg", result.get(0).getImageUrl());

        // 두 번째 장소 검증
        assertEquals("2def", result.get(1).getPlaceId());
        assertEquals("http://example.com/image2.jpg", result.get(1).getImageUrl());

        // 세 번째 장소 검증 (이미지 없음)
        assertEquals("3ghi", result.get(2).getPlaceId());
        assertNull(result.get(2).getImageUrl());  // 이미지가 없는 경우 null
    }
}
