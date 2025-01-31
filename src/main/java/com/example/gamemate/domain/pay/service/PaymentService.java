package com.example.gamemate.domain.pay.service;

import com.example.gamemate.domain.pay.dto.*;
import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.domain.user.enums.Role;
import com.example.gamemate.domain.user.repository.UserRepository;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.domain.pay.entity.Payment;
import com.example.gamemate.domain.pay.enums.PaymentStatus;
import com.example.gamemate.domain.pay.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PortoneService portoneService;
    private final WebClient webClient;

    /**
     * 결제 추가
     */
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto,
                                            User loginUser) {

        User user = userRepository.findById(loginUser.getId()).orElseThrow(
                () -> new ApiException(ErrorCode.USER_NOT_FOUND));

        String token = portoneService.portoneToken();

        Payment payment = new Payment(
                paymentRequestDto.getPayUid(),
                paymentRequestDto.getPortoneUid(),
                paymentRequestDto.getAmount(),
                paymentRequestDto.getStatus(),
                paymentRequestDto.getDetails(),
                user
        );

        // 결제 정보 단건 조회
        PaidPaymentResponseDto responseEntity = getPaymentData(paymentRequestDto.getPayUid(), token);
        BigDecimal totalAmount = BigDecimal.valueOf(responseEntity.getAmount().getTotal());

        // 요청금액과 결제 금액이 같은지, 결제에 성공한 상태인지 검증
        if (payment.getAmount().compareTo(totalAmount) == 0 && responseEntity.getStatus().equals("PAID")) {
            payment = paymentRepository.save(payment);

        } else {
            PaymentCancelRequestDto cancelRequest = new PaymentCancelRequestDto(
                    payment.getId(),
                    payment.getAmount(),
                    "결제 금액 오류"
            );
            cancelPayment(cancelRequest);

            throw new ApiException(ErrorCode.GAME_NOT_FOUND);//일심 수정 필요
        }

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto(
                payment.getPayUid(),
                payment.getPortoneUid(),
                payment.getAmount(),
                payment.getDetails(),
                payment.getPayStatus()
        );

        return paymentResponseDto;
    }


    /**
     * 결제 취소
     */
    @Transactional
    public PaymentResponseDto cancelPayment(PaymentCancelRequestDto paymentCancelRequestDto) {
        Payment payment = paymentRepository.findById(paymentCancelRequestDto.getPaymentId()).orElseThrow();
        String token = portoneService.portoneToken();

        // 결제 정보 단건 조회
        PaidPaymentResponseDto responseEntity = getPaymentData(payment.getPayUid(), token);
        BigDecimal totalAmount = BigDecimal.valueOf(responseEntity.getAmount().getTotal());

        // 환불 요청금액이 DB 와 같은지, 포트원에 넘어간 금액과 같은지 검증
        if (payment.getAmount().compareTo(paymentCancelRequestDto.getAmount()) != 0 || totalAmount.compareTo(paymentCancelRequestDto.getAmount()) != 0) {
            throw new ApiException(ErrorCode.GAME_NOT_FOUND);//수정 필요
        }

        if (payment.getPayStatus().equals(PaymentStatus.PAID)) {
            // 결제 취소
            String url = "/payments/" + payment.getPayUid() + "/cancel";

            payment.updatePayStatus(PaymentStatus.CANCELLED, paymentCancelRequestDto.getReason());

            webClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + token)
                    .body(Mono.just(Map.of("reason", paymentCancelRequestDto.getReason())), Map.class)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            PaymentResponseDto paymentResponseDto = new PaymentResponseDto(
                    payment.getPayUid(),
                    payment.getPortoneUid(),
                    payment.getAmount(),
                    payment.getDetails(),
                    payment.getPayStatus()
            );

            return paymentResponseDto;
        } else {
            throw new ApiException(ErrorCode.GAME_NOT_FOUND);//수정 필요
        }
    }


    /**
     * 결제 조회
     * 1.
     * 2. 전체 검색의 경우 일반 유저 사용
     */
    public PaymentListResponseDto getAllPayments(User loginUser, String type, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PaymentGetResponseDto> payments;


        if (type != null && !type.isEmpty()) {
            if (loginUser.getRole().equals(Role.ADMIN)) {
                payments = paymentRepository.findAllPayments(pageable);
            } else {
                throw new ApiException(ErrorCode.FORBIDDEN);
            }
        } else {
            if (loginUser.getRole().equals(Role.USER)) {
                payments = paymentRepository.findByUserData(loginUser.getId(), pageable);
            } else {
                throw new ApiException(ErrorCode.FORBIDDEN);
            }
        }

        return new PaymentListResponseDto(page, size, payments.getTotalElements(), payments.getTotalPages(), payments.getContent());
    }

    /**
     * 검증을 위한 결제 단건 조회
     */
    public PaidPaymentResponseDto getPaymentData(String paymentId, String token) {
        return webClient.get()
                .uri("/payments/" + paymentId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(PaidPaymentResponseDto.class)
                .block();
    }
}
