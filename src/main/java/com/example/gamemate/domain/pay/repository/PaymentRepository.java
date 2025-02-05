package com.example.gamemate.domain.pay.repository;

import com.example.gamemate.domain.pay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentUid(String paymentUid);
}


