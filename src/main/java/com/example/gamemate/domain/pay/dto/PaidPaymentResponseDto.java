package com.example.gamemate.domain.pay.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaidPaymentResponseDto {
    private final String status;
    private final String id;
    private final String transactionId;
    private final String merchantId;
    private final PaidPaymentAmountDto amount;
}
