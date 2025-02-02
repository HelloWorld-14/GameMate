package com.example.gamemate.domain.pay.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PortonePaymentResponseDto {
    private String impUid;         // 포트원 거래 고유번호
    private String merchantUid;     // 가맹점 주문번호
    private BigDecimal amount;     // 실제 결제 금액
    private String status;         // 결제 상태 (paid, ready, failed)
    private String payMethod;      // 결제 수단 (card, trans 등)
    private LocalDateTime paidAt;  // 결제 완료 시간
    private String errorCode;      // 오류 코드 (실패 시)
    private String errorMsg;       // 오류 메시지 (실패 시)

    // Getter/Setter 생략
}

