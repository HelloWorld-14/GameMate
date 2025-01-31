package com.example.gamemate.domain.pay.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class PaymentCancelRequestDto {
    private final Long paymentId;
    private final BigDecimal amount;
    private final String reason;
}
