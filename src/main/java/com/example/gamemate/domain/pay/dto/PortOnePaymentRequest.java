package com.example.gamemate.domain.pay.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class PortOnePaymentRequest {
    private String paymentUid;
    private BigDecimal amount;
    private String orderName;
}


