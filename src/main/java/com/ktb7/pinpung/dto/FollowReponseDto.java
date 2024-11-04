package com.ktb7.pinpung.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FollowReponseDto {
    private Integer count;
    private List<SimpleUserDto> follows;
}
