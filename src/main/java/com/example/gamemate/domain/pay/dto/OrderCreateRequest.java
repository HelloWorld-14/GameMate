package com.example.gamemate.domain.pay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @NotBlank(message = "주문명은 필수 항목입니다.")
    @Size(max = 100)
    private String orderName;

    @Positive(message = "금액은 양수여야 합니다.")
    private BigDecimal totalAmount;
}
