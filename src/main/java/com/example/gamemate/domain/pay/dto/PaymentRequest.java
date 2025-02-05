package com.example.gamemate.domain.pay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull
    private  Long orderId;

    @NotBlank
    private String orderName;

    @Positive
    private BigDecimal amount;
}