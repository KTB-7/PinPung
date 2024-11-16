package com.ktb7.pinpung.dto.Pung;

import com.ktb7.pinpung.entity.Pung;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class PungsResponseDto {
    private Integer count;
    private Integer currentPage;
    private List<Pung> pungs;

    // for test
    public Integer getPungCount() {
        return count;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public List<Pung> getPungs() {
        return pungs;
    }
}
