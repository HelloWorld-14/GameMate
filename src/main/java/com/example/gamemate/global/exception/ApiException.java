package com.example.gamemate.global.exception;

import com.example.gamemate.global.constant.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
