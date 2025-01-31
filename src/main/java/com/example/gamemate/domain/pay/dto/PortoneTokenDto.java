package com.example.gamemate.domain.pay.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public class PortoneTokenDto {
    private String accessToken;
    private String refreshToken;
}
