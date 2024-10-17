package com.ktb7.pinpung.service;

import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.repository.PungRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.eq;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PungServiceTest {

    @Mock
    private PungRepository pungRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private PungService pungService;

    private final Pageable pageable = PageRequest.of(0, 3);

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
    @DisplayName("펑 모음 가져오기")
    void testGetPlacesWithRepresentativeImage() {
        // Given
        Pung pung1 = new Pung();
        Pung pung2 = new Pung();
        Pung pung3 = new Pung();
        List<Pung> pungs = Arrays.asList(pung1, pung2, pung3);

        Page<Pung> pungsPage = new PageImpl<>(pungs, pageable, 3);
        when(pungRepository.findByPlaceIdAndCreatedAtAfter(anyString(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pungsPage);
        // When
        PungsResponseDto response = pungService.getPungsByPlaceId("testPlaceId", pageable);

        // Then
        assertEquals(3, response.getPungCount());
        assertEquals(0, response.getCurrentPage());
        assertEquals(3, response.getPungs().size());
    }

    @Test
    @DisplayName("펑 모음 불러오기-페이지가 넘어가는 경우")
    void testGetPungsByPlaceIdWithMultiplePages() {
        // Given
        Pung pung1 = new Pung();
        Pung pung2 = new Pung();
        Pung pung3 = new Pung();
        Pung pung4 = new Pung();
        List<Pung> firstPagePungs = Arrays.asList(pung1, pung2, pung3);
        List<Pung> secondPagePungs = Arrays.asList(pung4);

        // 첫 번째 페이지
        Page<Pung> firstPage = new PageImpl<>(firstPagePungs, PageRequest.of(0, 3), 4);
        when(pungRepository.findByPlaceIdAndCreatedAtAfter(anyString(), any(LocalDateTime.class), eq(PageRequest.of(0, 3))))
                .thenReturn(firstPage);

        // 두 번째 페이지
        Page<Pung> secondPage = new PageImpl<>(secondPagePungs, PageRequest.of(1, 3), 4);
        when(pungRepository.findByPlaceIdAndCreatedAtAfter(anyString(), any(LocalDateTime.class), eq(PageRequest.of(1, 3))))
                .thenReturn(secondPage);

        // When - 첫 번째 페이지 요청
        PungsResponseDto firstPageResponse = pungService.getPungsByPlaceId("testPlaceId", PageRequest.of(0, 3));
        // Then
        assertEquals(4, firstPageResponse.getPungCount());
        assertEquals(0, firstPageResponse.getCurrentPage());  // 첫 번째 페이지
        assertEquals(3, firstPageResponse.getPungs().size());  // 첫 번째 페이지에는 3개의 펑이 있음

        // When - 두 번째 페이지 요청
        PungsResponseDto secondPageResponse = pungService.getPungsByPlaceId("testPlaceId", PageRequest.of(1, 3));
        // Then
        assertEquals(4, secondPageResponse.getPungCount());
        assertEquals(1, secondPageResponse.getCurrentPage());  // 두 번째 페이지
        assertEquals(1, secondPageResponse.getPungs().size());  // 두 번째 페이지에는 1개의 펑이 있음
    }
}
