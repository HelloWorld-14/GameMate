package com.example.gamemate.domain.pay.controller;

import com.example.gamemate.global.config.auth.CustomUserDetails;
import com.example.gamemate.domain.pay.dto.PaymentCancelRequestDto;
import com.example.gamemate.domain.pay.dto.PaymentListResponseDto;
import com.example.gamemate.domain.pay.dto.PaymentRequestDto;
import com.example.gamemate.domain.pay.dto.PaymentResponseDto;
import com.example.gamemate.domain.pay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * 결제 저장
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @RequestBody PaymentRequestDto paymentRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto, customUserDetails.getUser());
        return new ResponseEntity<>(paymentResponseDto, HttpStatus.CREATED);
    }

    /**
     * 결제 취소(관리자만 가능)
     */
    @PostMapping("/cancel")
    public ResponseEntity<PaymentResponseDto> cancelPayment(
            @RequestBody PaymentCancelRequestDto paymentCancelRequestDto
    ) {
        PaymentResponseDto paymentResponseDto = paymentService.cancelPayment(paymentCancelRequestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 결제 내역 조회
     */
    @GetMapping
    public ResponseEntity<PaymentListResponseDto> getPayment(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String payType,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {


        PaymentListResponseDto payment = paymentService.getAllPayments(customUserDetails.getUser(), payType, page, size);

      //  return new ResponseEntity<>(new CommonResDto<>("결제 조회 완료.", payment), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}