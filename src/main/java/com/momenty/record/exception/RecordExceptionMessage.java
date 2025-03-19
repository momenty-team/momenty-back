package com.momenty.record.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RecordExceptionMessage {
    BAD_RECORD_METHOD("잘못된 기록 방식입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_RECORD("기록을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_RECORD_OPTION("옵션을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_RECORD_UNIT("단위를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_RECORD_DETAIL("기록 데이터를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_RECORD_OPTION("기록 방식이 옵션이 아닙니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_NEED_UNIT("단위가 필요한 기록 방식이 아닙니다.", HttpStatus.BAD_REQUEST),
    USED_OPTION_NOT_DELETE("사용중인 옵션은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    ;

    private final String message;
    private final HttpStatus status;

    RecordExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
