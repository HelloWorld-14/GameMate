package com.example.gamemate.domain.pay.entity;

import com.example.gamemate.domain.pay.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentUid;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne(mappedBy = "payment")
    private PayOrder payOrder;  // 역방향 매핑

    @Builder
    public Payment(String paymentUid, BigDecimal amount, PaymentStatus status,PayOrder payOrder) {
        this.paymentUid = paymentUid;
        this.amount = amount;
        this.status = status;
        this.payOrder = payOrder;
    }


    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public static Payment create() {
        return new Payment();
    }

    // 정적 팩토리 메소드 추가
    public static Payment createDraft() {
        return Payment.builder()
                .status(PaymentStatus.READY)
                .build();
    }
}
