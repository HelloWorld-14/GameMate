package com.example.gamemate.domain.pay.service;

import com.example.gamemate.domain.pay.dto.*;
import com.example.gamemate.domain.pay.dto.PortOneErrorDto;
import com.example.gamemate.domain.pay.entity.PayOrder;
import com.example.gamemate.domain.pay.entity.Payment;
import com.example.gamemate.domain.pay.repository.PayOrderRepository;
import com.example.gamemate.domain.pay.repository.PaymentRepository;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.global.exception.PortOneApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayOrderRepository payOrderRepository;
    private final WebClient portOneWebClient; // PortOneConfig에서 주입받은 WebClient
//
//    public PaymentResponse requestPayment(Long orderId, User loginUser) {
//
//        PayOrder order = payOrderRepository.findByIdWithPayment(orderId)
//                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND));// 추후 수정
//
//        PortOnePaymentRequest apiRequest = buildPortOneRequest(order);
//
//        PortOnePaymentResponse portOneResponse = portOneWebClient.post()
//                .uri("/payments") // PortOneConfig의 baseUrl에 /v2가 포함되어 있음
//                .bodyValue(apiRequest)
//                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, response ->
//                        response.bodyToMono(PortOneErrorDto.class)
//                                .flatMap(error -> {
//                                    log.error("PortOne API 오류 [{}]: {}", error.getCode(), error.getMessage());
//                                    return Mono.error(new PortOneApiException(error));
//                                })
//                )
//                .onStatus(HttpStatusCode::is5xxServerError, response ->
//                        Mono.error(new ApiException(ErrorCode.INTERNAL_SERVER_ERROR))  // 5xx 오류 추가 처리
//                )
//                .bodyToMono(PortOnePaymentResponse.class)
//                .block();
//
//
//        Payment payment = order.getPayment();
//        payment.completePayment(
//                portOneResponse.getPaymentId(),
//                portOneResponse.getAmount(),
//                PortOneStatusMapper.toDomain(portOneResponse.getStatus())
//        );
//
//        return PaymentResponse.from(paymentRepository.save(payment));
//    }

    private PortOnePaymentRequest buildPortOneRequest(PayOrder order) {
        return PortOnePaymentRequest.builder()
                .paymentUid(UUID.randomUUID().toString())
                .amount(order.getTotalAmount())
                .orderName(order.getOrderName())
                .build();
    }

    public void handleWebhook(PortOneWebhookRequest request) {
        Payment payment = paymentRepository.findByPaymentUid(request.getPaymentUid())
                .orElseThrow(() -> new ApiException(ErrorCode.GAME_NOT_FOUND)); //추후 수정

        payment.updateStatus(PortOneStatusMapper.toDomain(request.getStatus()));
        paymentRepository.save(payment);

        log.info("[Webhook 처리 완료] Payment {} 상태: {}", payment.getPaymentUid(), payment.getStatus());
    }
}
