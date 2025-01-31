package com.example.gamemate.domain.pay.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PaymentListResponseDto {
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final List<PaymentGetResponseDto> data;
}