package com.example.gamemate.domain.pay.entity;

import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.common.BaseEntity;
import com.example.gamemate.domain.pay.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pay_uid", nullable = false, unique = true)
    private String payUid;

    @Column(name = "portone_uid", nullable = false, unique = true)
    private String portoneUid;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "pay_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus payStatus;

    @Column(name = "details", nullable = false)
    private String details;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime updatedAt;


    public Payment(String payUid, String portoneUid, BigDecimal amount , PaymentStatus payStatus, String details, User user) {
        this.payUid = payUid;
        this.portoneUid = portoneUid;
        this.amount = amount;

        this.payStatus = payStatus;
        this.details = details;
        this.user = user;
    }

    public void updatePayStatus(PaymentStatus payStatus, String details) {
        this.payStatus = payStatus;
        this.details = details;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}