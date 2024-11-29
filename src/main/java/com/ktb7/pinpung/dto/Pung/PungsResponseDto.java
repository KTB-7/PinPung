package com.ktb7.pinpung.dto.Pung;

import com.ktb7.pinpung.entity.Pung;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PungsResponseDto {
    private Integer count;
    private Integer currentPage;
    private List<PungDto> pungs;
}
