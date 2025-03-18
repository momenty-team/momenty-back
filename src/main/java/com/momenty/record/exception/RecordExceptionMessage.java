package com.momenty.record.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RecordExceptionMessage {
    BAD_RECORD_METHOD("잘못된 기록 방식입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_RECORD("기록을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String message;
    private final HttpStatus status;

    RecordExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
