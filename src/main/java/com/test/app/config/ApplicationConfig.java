package com.test.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class ApplicationConfig {

    @Bean
    WebClient webClient() {
        return WebClient.create("https://api.github.com");
    }

}
