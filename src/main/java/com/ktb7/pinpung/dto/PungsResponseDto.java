package com.ktb7.pinpung.dto;

import com.ktb7.pinpung.entity.Pung;

import java.util.List;

public class PungsResponseDto {
    private Integer pungCount;
    private Integer currentPage;
    private List<Pung> pungs;

    public PungsResponseDto(Integer pungCount, Integer currentPage, List<Pung> pungs) {
        this.pungCount = pungCount;
        this.currentPage = currentPage;
        this.pungs = pungs;
    }

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
