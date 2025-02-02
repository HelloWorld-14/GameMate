package com.example.gamemate.domain.pay.controller;

import com.example.gamemate.domain.pay.dto.PaymentRequest;
import com.example.gamemate.domain.pay.dto.PaymentResponse;
import com.example.gamemate.domain.pay.dto.WebhookRequest;
import com.example.gamemate.domain.pay.service.PaymentService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:8080")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody @Valid PaymentRequest request) {
        log.info("Creating payment for request: {}", request);
        PaymentResponse response = paymentService.requestPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{impUid}/verify")
    public ResponseEntity<Void> verifyPayment(@PathVariable String impUid) {
        log.info("Verifying payment with impUid: {}", impUid);
        try {
            paymentService.verifyPayment(impUid);
            return ResponseEntity.ok().build();
        } catch (IamportResponseException | IOException e) {
            log.error("Error verifying payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody WebhookRequest webhookRequest,
            @RequestHeader("X-ImpSignature") String signature) {
        log.info("Received webhook: {}", webhookRequest);
        if (!isValidSignature(signature, webhookRequest)) {
            log.warn("Invalid signature for webhook");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }
        try {
            paymentService.processWebhook(webhookRequest);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentResponse>> getPaymentHistory(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("Fetching payment history for user: {}", customUserDetails.getUser().getEmail());
        List<PaymentResponse> history = paymentService.getUserPayments(customUserDetails.getUser().getEmail());
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{impUid}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable String impUid) {
        log.info("Cancelling payment with impUid: {}", impUid);
        try {
            PaymentResponse response = paymentService.cancelPayment(impUid);
            return ResponseEntity.ok(response);
        } catch (IamportResponseException | IOException e) {
            log.error("Error cancelling payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidSignature(String signature, WebhookRequest webhookRequest) {
        // 시그니처 검증 로직 구현
        // 포트원 문서를 참조하여 구현해야 함
        return true; // 임시 반환값, 실제로는 검증 로직을 구현해야 함
    }
}
