package com.example.gamemate.domain.pay.service;

import com.example.gamemate.domain.pay.dto.*;
import com.example.gamemate.domain.pay.entity.PayOrder;
import com.example.gamemate.domain.pay.entity.Payment;
import com.example.gamemate.domain.pay.repository.PayOrderRepository;
import com.example.gamemate.domain.pay.repository.PaymentRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PayOrderRepository payOrderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request, User user) {
        // 결제 정보 먼저 생성 (트랜잭션 내부에서 처리)
        Payment payment = paymentRepository.save(Payment.createDraft());

        PayOrder order = PayOrder.builder()
                .orderName(request.getOrderName())
                .totalAmount(request.getTotalAmount())
                .user(user)
                .payment(payment)
                .build();

        return OrderResponse.from(payOrderRepository.save(order));
    }

    public OrderDetailResponse getOrderDetail(Long orderId) {
        PayOrder order = payOrderRepository.findByIdWithPayment(orderId)
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));//추후 수정

        return OrderDetailResponse.from(order);
    }

}
