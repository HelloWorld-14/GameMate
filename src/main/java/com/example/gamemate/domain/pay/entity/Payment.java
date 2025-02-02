package com.example.gamemate.domain.pay.entity;

import com.example.gamemate.domain.pay.dto.PaymentResponse;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseEntity;
import com.example.gamemate.domain.pay.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String impUid;  // 포트원 결제고유번호
    private String merchantUid; // 주문번호


    private BigDecimal amount;

    private String buyerEmail;

    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentResponse toResponse() {
        return PaymentResponse.builder()
                .impUid(this.impUid)
                .merchantUid(this.merchantUid)
                .amount(this.amount)
                .paidAt(this.paidAt)
                .status(this.status)
                .build();
    }
}
