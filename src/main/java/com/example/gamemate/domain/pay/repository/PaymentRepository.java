package com.example.gamemate.domain.pay.repository;


import com.example.gamemate.domain.pay.dto.PaymentGetResponseDto;
import com.example.gamemate.domain.pay.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p join fetch p.user u where u.id = :userId order by p.createdAt desc")
    Page<PaymentGetResponseDto> findByUserData(Long userId, Pageable pageable);

    @Query("SELECT new com.example.gamemate.domain.pay.dto.PaymentGetResponseDto(p) FROM Payment p")
    Page<PaymentGetResponseDto> findAllPayments(Pageable pageable);

}

