package com.ktb7.pinpung.oauth2.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class LogoutSuccessController {

    @GetMapping("/logout-success")
    public void logoutSuccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://pinpung.net/login");
    }
}
