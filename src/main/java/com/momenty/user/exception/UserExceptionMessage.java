package com.momenty.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserExceptionMessage {
    DUPLICATION_NICKNAME("증복된 닉네임입니다.", HttpStatus.CONFLICT),
    AUTHENTICATION("잘못된 인증입니다.", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_USER("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;

    UserExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
