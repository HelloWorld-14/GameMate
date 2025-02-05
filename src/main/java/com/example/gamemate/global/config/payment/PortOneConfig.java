package com.example.gamemate.global.config.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PortOneConfig {
    @Value("${portone.api-secret}")
    private String apiSecret;

    @Bean
    public WebClient portOneClient() {
        return WebClient.builder()
                .baseUrl("https://api.portone.io")
                .defaultHeader("Authorization", "PortOne " + apiSecret)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Value("${portone.store-id}")
    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    @Bean
    public WebClient portOneWebClient(
            @Value("${portone.api-secret}") String apiSecret
    ) {
        return WebClient.builder()
                .baseUrl("https://api.portone.io/v2")
                .defaultHeader("Authorization", "PortOne " + apiSecret) // 로그[1]에서 누락된 헤더 추가
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
                )
                .build();
    }
}

