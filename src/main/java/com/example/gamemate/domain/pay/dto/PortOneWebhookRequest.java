package com.example.gamemate.domain.pay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortOneWebhookRequest {
    private String paymentUid;
    private String status;
}

