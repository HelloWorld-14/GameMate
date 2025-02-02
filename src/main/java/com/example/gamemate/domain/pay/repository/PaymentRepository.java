package com.example.gamemate.domain.pay.repository;

import com.example.gamemate.domain.pay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByImpUid(String impUid);
    Optional<Payment> findByMerchantUid(String merchantUid);
    List<Payment> findByBuyerEmail(String email);
}


