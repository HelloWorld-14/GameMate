package com.example.gamemate.domain.pay.entity;

import com.example.gamemate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payorder")
public class PayOrder {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;  // 필수 관계 설정

    private LocalDateTime orderDate;

    private BigDecimal totalAmount;

    @Builder
    public PayOrder(String orderName, User user, BigDecimal totalAmount, Payment payment) {
        this.orderName = orderName;
        this.user = user;
        this.totalAmount = totalAmount;
        this.payment = payment;
        this.orderDate = LocalDateTime.now();

    }

}


