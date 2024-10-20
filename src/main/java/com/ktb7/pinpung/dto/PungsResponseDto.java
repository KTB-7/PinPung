package com.ktb7.pinpung.dto;

import com.ktb7.pinpung.entity.Pung;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class PungsResponseDto {
    private Integer pungCount;
    private Integer currentPage;
    private List<Pung> pungs;

    // for test
    public Integer getPungCount() {
        return pungCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public List<Pung> getPungs() {
        return pungs;
    }
}
