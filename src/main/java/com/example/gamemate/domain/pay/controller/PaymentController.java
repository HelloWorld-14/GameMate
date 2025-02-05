package com.example.gamemate.domain.pay.controller;

import com.example.gamemate.domain.pay.dto.PaymentResponse;
import com.example.gamemate.domain.pay.dto.PortOneWebhookRequest;
import com.example.gamemate.domain.pay.service.PaymentService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "결제 관리", description = "결제 처리 및 상태 관리 API")
public class PaymentController {
    private final PaymentService paymentService;

//    // 결제 요청 시작 (PortOne API 호출)
//    @PostMapping("/{orderId}")
//    @Operation(summary = "결제 프로세스 시작")
//    public ResponseEntity<PaymentResponse> startPayment(@PathVariable Long orderId,
//                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
//
//        return ResponseEntity.ok(paymentService.requestPayment(orderId, userDetails.getUser()));
//    }
//
//    // 결제 상태 조회 (추가)
//    @GetMapping("/{paymentUid}")
//    @Operation(summary = "결제 상태 조회")
//    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(
//            @PathVariable String paymentUid
//    ) {
//        return ResponseEntity.ok(paymentService.getPaymentStatus(paymentUid));
//    }
//
//    // 결제 내역 조회 (추가)
//    @GetMapping
//    @Operation(summary = "사용자 결제 내역 조회")
//    public ResponseEntity<Page<PaymentHistoryResponse>> getPaymentHistory(
//            @PageableDefault(size = 10) Pageable pageable,
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ) {
//        return ResponseEntity.ok(paymentService.getPaymentHistory(userDetails.getUser().getId(), pageable));
//    }
//
//    // 결제 취소 (추가)
//    @PostMapping("/{paymentUid}/cancel")
//    @Operation(summary = "결제 취소 요청")
//    public ResponseEntity<PaymentCancelResponse> cancelPayment(
//            @PathVariable String paymentUid,
//            @Valid @RequestBody PaymentCancelRequest request
//    ) {
//        return ResponseEntity.ok(paymentService.cancelPayment(paymentUid, request.getReason()));
//    }

    // PortOne 웹훅 처리
    @PostMapping("/callback")
    @Operation(hidden = true) // 문서에서 숨김
    public ResponseEntity<Void> handlePaymentCallback(
            @RequestBody PortOneWebhookRequest request
    ) {
        paymentService.handleWebhook(request);
        return ResponseEntity.ok().build();
    }
}
