package com.ktb7.pinpung.dto;

import com.ktb7.pinpung.entity.Pung;

import java.util.List;

public class PungsResponseDto {
    private List<Pung> pungs;

    // 생성자 추가
    public PungsResponseDto(List<Pung> pungs) {
        this.pungs = pungs;
    }

    // for test
    public List<Pung> getPungs() {
        return pungs;
    }
}
