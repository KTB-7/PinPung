package com.ktb7.pinpung.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TasteRequestDto {
    private Integer age;
    private List<String> activities;
    private List<String> menus;
}
