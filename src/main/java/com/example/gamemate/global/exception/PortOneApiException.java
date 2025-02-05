package com.example.gamemate.global.exception;


import com.example.gamemate.domain.pay.dto.PortOneErrorDto;

public class PortOneApiException extends RuntimeException {

    private  String errorCode;

    public PortOneApiException(String message) {
        super(message);
    }

    // 필요 시 원인(Throwable)을 포함하는 생성자 추가
    public PortOneApiException(String message, Throwable cause) {
        super(message, cause);
    }



    // PortOneErrorDto를 받는 생성자
    public PortOneApiException(PortOneErrorDto error) {
        super(error.getMessage());  // 메시지 설정
        this.errorCode = error.getCode();  // 에러 코드 저장
    }

    public String getErrorCode() {
        return errorCode;
    }
}

