package com.example.gamemate.domain.pay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    @NotNull
    @DecimalMin("1000")
    private BigDecimal amount;

    @NotBlank
    @Email
    private String buyerEmail;
}

