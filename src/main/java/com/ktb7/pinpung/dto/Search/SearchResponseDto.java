package com.ktb7.pinpung.dto.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResponseDto {
    private Long userId;
    private String sortType;
    private Long count;
    private List<SearchPlaceInfoDto> searchPlaceInfoDtoList;
}
