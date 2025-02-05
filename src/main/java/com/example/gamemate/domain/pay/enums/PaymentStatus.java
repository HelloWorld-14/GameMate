package com.example.gamemate.domain.pay.enums;

public enum PaymentStatus {
    READY,
    PAID,
    FAILED,
    CANCELED;

    public static PaymentStatus fromPortOneStatus(String status) {
        return switch (status.toUpperCase()) {
            case "PAID" -> PAID;
            case "FAILED" -> FAILED;
            case "CANCELED" -> CANCELED;
            default -> READY;
        };
    }
}
