package com.example.gamemate.domain.pay.dto;

import com.example.gamemate.domain.pay.entity.Payment;
import com.example.gamemate.domain.pay.enums.PaymentStatus;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentResponse {
    private String paymentUid;
    private BigDecimal amount;
    private Long userId;
    private PaymentStatus status;

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentUid(),
                payment.getAmount(),
                payment.getPayOrder().getUser().getId(), // Order → User 접근
                payment.getStatus()
        );
    }
}
