package com.momenty.global.auth.jwt.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TokenExceptionMessage {
    INVALID_TOKEN("유효하지 않은 토큰이거나 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final String message;
    private final HttpStatus status;

    TokenExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
