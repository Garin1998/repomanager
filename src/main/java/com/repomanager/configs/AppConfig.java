package com.repomanager.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient getWebClientForGH() {
        return WebClient.builder()
                .baseUrl("https://api.github.com/")
                .build();

    }

}
