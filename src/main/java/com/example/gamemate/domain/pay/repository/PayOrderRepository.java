package com.example.gamemate.domain.pay.repository;

import com.example.gamemate.domain.pay.entity.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayOrderRepository extends JpaRepository<PayOrder, Long> {

    @Query("SELECT o FROM PayOrder o INNER JOIN FETCH o.payment WHERE o.id = :orderId")
    Optional<PayOrder> findByIdWithPayment(@Param("orderId") Long orderId);
}
