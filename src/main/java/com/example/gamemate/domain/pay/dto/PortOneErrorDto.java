package com.example.gamemate.domain.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortOneErrorDto {
    private String code;
    private String message;
    private Map<String, Object> params; // 에러 상세 정보 (필요시 확장)

    public String getCode() { return code; }
    public String getMessage() { return message; }
}

