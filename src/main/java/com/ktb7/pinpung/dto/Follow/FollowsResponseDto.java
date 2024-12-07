package com.ktb7.pinpung.dto.Follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FollowsResponseDto {
    private Integer count;
    private List<SimpleUserDto> follows;
}
