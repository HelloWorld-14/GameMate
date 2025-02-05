package com.example.gamemate.domain.pay.service;

import com.example.gamemate.domain.pay.enums.PaymentStatus;

public class PortOneStatusMapper {
    public static PaymentStatus toDomain(String portOneStatus) {
        return switch (portOneStatus) {
            case "PAID" -> PaymentStatus.PAID;
            case "READY" -> PaymentStatus.READY;
            case "CANCELLED" -> PaymentStatus.CANCELED;
            default -> throw new IllegalArgumentException("Unknown status: " + portOneStatus);
        };
    }
}
