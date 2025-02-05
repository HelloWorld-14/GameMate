package com.example.gamemate.domain.pay.dto;

import com.example.gamemate.domain.pay.entity.PayOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String orderName;
    private LocalDateTime orderDate;

    public static OrderResponse from(PayOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderName(),
                order.getOrderDate()
        );
    }
}
