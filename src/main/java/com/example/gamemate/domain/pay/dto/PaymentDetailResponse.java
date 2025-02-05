package com.example.gamemate.domain.pay.dto;

import com.example.gamemate.domain.pay.entity.Payment;
import com.example.gamemate.domain.pay.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentDetailResponse {
    private String paymentUid;
    private BigDecimal amount;
    private PaymentStatus status;

    public static PaymentDetailResponse from(Payment payment) {
        return PaymentDetailResponse.builder()
                .paymentUid(payment.getPaymentUid())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }
}
