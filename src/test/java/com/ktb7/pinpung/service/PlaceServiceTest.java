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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
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
    private PlaceRepository placeRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private Clock clock;

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
    @DisplayName("대표 펑 이미지 가져오기")
    void testGetPlacesWithRepresentativeImage() {
        // 테스트용 장소 ID 목록
        List<String> placeIds = Arrays.asList("1abc", "2def", "3ghi");
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        // PungRepository 모킹
        Pung pung1 = new Pung();
        pung1.setImageUrl("http://example.com/image1.jpg");

        Pung pung2 = new Pung();
        pung2.setImageUrl("http://example.com/image2.jpg");

        when(pungRepository.findLatestByPlaceIdWithin24Hours("1abc", yesterday))
                .thenReturn(Optional.of(pung1));

        when(pungRepository.findLatestByPlaceIdWithin24Hours("2def", yesterday))
                .thenReturn(Optional.of(pung2));

        when(pungRepository.findLatestByPlaceIdWithin24Hours("3ghi", yesterday))
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

    @Test
    @DisplayName("place info, review, tags, rep pung 가져오기")
    void testGetPlaceInfo() {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        // PlaceRepository 모킹
        Place place = new Place();
        place.setPlaceId("1abc");
        place.setPlaceName("Test Place");
        place.setAddress("123 Test address");
        when(placeRepository.findById("1abc")).thenReturn(Optional.of(place));

        // TagRepository 모킹
        when(tagRepository.findTagsByPlaceIds(Collections.singletonList("1abc")))
                .thenReturn(Arrays.asList(new Object[] { "1abc", "Coffee" }, new Object[] { "1abc", "Quiet" }));

        // PungRepository 모킹
        Pung pung = new Pung();
        pung.setImageUrl("http://example.com/image1.jpg");
        pung.setText("Test Pung");
        when(pungRepository.findLatestByPlaceIdWithin24Hours("1abc", yesterday))
                .thenReturn(Optional.of(pung));

        // ReviewRepository 모킹
        Review review = new Review();
        review.setUserId(1);
        review.setText("Great place!");
        review.setCreatedAt(LocalDateTime.now(clock));
        when(reviewRepository.findByPlaceId("1abc")).thenReturn(Collections.singletonList(review));

        // 테스트 실행
        PlaceInfoResponseDto result = placeService.getPlaceInfo("1abc");

        // 결과 검증
        assertEquals("1abc", result.getPlaceId());
        assertEquals("Test Place", result.getPlaceName());
        assertEquals("123 Test address", result.getAddress());
        assertEquals(2, result.getTags().size());
        assertEquals("Coffee", result.getTags().get(0));
        assertEquals("Quiet", result.getTags().get(1));
        assertEquals(1, result.getReviews().size());
        assertEquals("Great place!", result.getReviews().get(0).getText());
        assertEquals("http://example.com/image1.jpg", result.getRepresentativePung().getImageUrl());
        assertEquals("Test Pung", result.getRepresentativePung().getText());
    }
}
