package com.example.gamemate.domain.pay.dto;

import com.example.gamemate.domain.pay.entity.PayOrder;
import com.example.gamemate.domain.pay.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long orderId;
    private String orderName;
    private BigDecimal totalAmount;
    private PaymentDetailResponse payment; // Payment 상세 정보 포함

    public static OrderDetailResponse from(PayOrder order) {
        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .orderName(order.getOrderName())
                .totalAmount(order.getTotalAmount())
                .payment(PaymentDetailResponse.from(order.getPayment()))
                .build();
    }

}
