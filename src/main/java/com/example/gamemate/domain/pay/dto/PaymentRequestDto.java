package com.example.gamemate.domain.pay.dto;

import com.example.gamemate.domain.pay.enums.PaymentStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor

public class PaymentRequestDto {

    private final String payUid;
    private final String portoneUid;
    private final BigDecimal amount;
    private final String details;
    private final PaymentType type;
    private final PaymentStatus status;
}
