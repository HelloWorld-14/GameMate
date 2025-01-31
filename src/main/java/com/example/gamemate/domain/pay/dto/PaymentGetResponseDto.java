package com.example.gamemate.domain.pay.dto;


import com.example.gamemate.domain.pay.entity.Payment;
import com.example.gamemate.domain.pay.enums.PaymentStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PaymentGetResponseDto {
    private final Long id;
    private final String payUid;
    private final String portoneUid;
    private final BigDecimal amount;
    private final String details;
    private final PaymentStatus status;
    private final String userEmail;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PaymentGetResponseDto(Payment payment) {
        this.id = payment.getId();
        this.payUid = payment.getPayUid();
        this.portoneUid = payment.getPortoneUid();
        this.amount = payment.getAmount();
        this.details = payment.getDetails();
        this.status = payment.getPayStatus();
        this.userEmail = payment.getUser().getEmail();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
    }
}
