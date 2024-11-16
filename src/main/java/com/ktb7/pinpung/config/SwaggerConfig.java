package com.ktb7.pinpung.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pinpung API")
                        .version("1.0.0")
                        .description("Pinpung 프로젝트를 위한 REST API 문서입니다.")
                        .contact(new Contact()
                                .name("Pinpung")
                                .url("https://pinpung.net")));
    }
}
