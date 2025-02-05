package com.example.gamemate.domain.pay.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PortOnePaymentResponse {
    private String paymentId;
    private String status;
    private BigDecimal amount;
    private String transactionId;
}
