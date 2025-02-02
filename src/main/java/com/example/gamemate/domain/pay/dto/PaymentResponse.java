package com.example.gamemate.domain.pay.dto;

import com.example.gamemate.domain.pay.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private String impUid;
    private String merchantUid;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private PaymentStatus status;

}