package com.example.gamemate.domain.pay.controller;

import com.example.gamemate.domain.pay.dto.OrderCreateRequest;
import com.example.gamemate.domain.pay.dto.OrderDetailResponse;
import com.example.gamemate.domain.pay.dto.OrderResponse;
import com.example.gamemate.domain.pay.service.OrderService;
import com.example.gamemate.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "주문 관리", description = "주문 생성 및 조회 API")
public class OrderController {
    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    @Operation(summary = "새 주문 생성")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(orderService.createOrder(request, userDetails.getUser()));
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 정보 조회")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }
//
//    // 주문 목록 조회 (추가)
//    @GetMapping
//    @Operation(summary = "사용자 주문 목록 조회")
//    public ResponseEntity<Page<OrderSummaryResponse>> getOrders(
//            @PageableDefault(size = 10) Pageable pageable,
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ) {
//        return ResponseEntity.ok(orderService.getUserOrders(userDetails.getUser().getId(), pageable));
//    }
//
//    // 주문 상태 변경 (추가)
//    @PatchMapping("/{orderId}/status")
//    @Operation(summary = "주문 상태 업데이트")
//    public ResponseEntity<OrderStatusResponse> updateOrderStatus(
//            @PathVariable Long orderId,
//            @Valid @RequestBody OrderStatusUpdateRequest request
//    ) {
//        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getStatus()));
//    }
//
//    // 주문 취소 (추가)
//    @PostMapping("/{orderId}/cancel")
//    @Operation(summary = "주문 취소")
//    public ResponseEntity<OrderCancelResponse> cancelOrder(
//            @PathVariable Long orderId,
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ) {
//        return ResponseEntity.ok(orderService.cancelOrder(orderId, userDetails.getUser()));
//    }
}


