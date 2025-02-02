package com.example.gamemate.domain.pay.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WebhookRequest {
    private String imp_uid;
    private String merchant_uid;
    private String status;

    // getters, setters, toString()
}
