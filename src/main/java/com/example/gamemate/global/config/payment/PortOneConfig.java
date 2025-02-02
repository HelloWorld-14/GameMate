package com.example.gamemate.global.config.payment;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PortOneConfig {

    @Value("${portone.imp-key}")  // 하이픈(-) 사용
    private String impKey;

    @Value("${portone.imp-secret}")
    private String impSecret;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(impKey, impSecret);
    }
}
