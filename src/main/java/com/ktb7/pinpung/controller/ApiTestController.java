package com.ktb7.pinpung.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTestController {

    @GetMapping("/api/test")
    public String testConnection() {
        return "API 통신이 성공적으로 이루어졌습니다!";
    }
}
