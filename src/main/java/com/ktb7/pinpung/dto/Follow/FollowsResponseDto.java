package com.ktb7.pinpung.dto.Follow;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FollowsResponseDto {
    private Integer count;
    private List<SimpleUserDto> follows;
}
