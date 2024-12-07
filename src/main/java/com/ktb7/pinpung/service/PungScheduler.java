package com.ktb7.pinpung.service;

import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.repository.PungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PungScheduler {
    private final PungRepository pungRepository;
    private final Clock clock;

    @Scheduled(cron = "0 0 6 * * *") // 매일 오전 6시에 실행
    public void schedule() {
        LocalDateTime yesterday = LocalDateTime.now(clock).minusDays(1);

        List<Pung> pungs = pungRepository.findCreatedIn24H(yesterday);

        for (Pung pung : pungs) {
            pung.setIsReview(true);
        }

        pungRepository.saveAll(pungs);
    }
}

