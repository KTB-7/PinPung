package com.ktb7.pinpung.service;

import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.dto.PungsResponseDto;
import com.ktb7.pinpung.repository.PungRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PungService {
    private final PungRepository pungRepository;
    private final Clock clock;

    public PungService(PungRepository pungRepository, Clock clock) {
        this.pungRepository = pungRepository;
        this.clock = clock;
    }

    /*
   GET pungs/{placeId}
   place id를 받아 해당 장소의 펑 모음 반환
   */
    public PungsResponseDto getPungsByPlaceId(Long placeId, Pageable pageable) {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);
        Page<Pung> pungsPage = pungRepository.findByPlaceIdAndCreatedAtAfter(placeId, yesterday, pageable);

        int pungCount = (int) pungsPage.getTotalElements();
        int currentPage = pungsPage.getNumber();
        log.info("pungs/{placeId} pungCount, currentPage: {} {}", pungCount, currentPage);

        return new PungsResponseDto(pungCount, currentPage, pungsPage.getContent());
    }
}
