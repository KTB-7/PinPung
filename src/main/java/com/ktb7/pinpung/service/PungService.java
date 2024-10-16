package com.ktb7.pinpung.service;

import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.repository.PungRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class PungService {
    private final PungRepository pungRepository;
    private final Clock clock;

    public PungService(PungRepository pungRepository, Clock clock) {
        this.pungRepository = pungRepository;
        this.clock = clock;
    }

    // placeId를 받아서 24시간 내의 펑을 페이지네이션을 적용해 가져오는 로직
    public PungsResponseDto getPungsByPlaceId(Long placeId, Pageable pageable) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);
        Page<Pung> pungsPage = pungRepository.findByPlaceIdAndCreatedAtAfter(placeId, yesterday, pageable);

        return new PungsResponseDto(pungsPage.getContent());
    }
}

