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
    METHOD_NOT_RECORD_NUMBER("기록 방식이 숫자가 아닙니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_RECORD_OX("기록 방식이 OX가 아닙니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_RECORD_TEXT("기록 방식이 텍스트가 아닙니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_NEED_UNIT("단위가 필요한 기록 방식이 아닙니다.", HttpStatus.BAD_REQUEST),
    USED_OPTION_NOT_DELETE("사용중인 옵션은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    TOO_MANY_PROMPT("AI 요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS),
    BAD_PERIOD("지원하지 않는 기간입니다.", HttpStatus.BAD_REQUEST),
    BAD_OPTION_ID("잘못된 option id 요청입니다.", HttpStatus.BAD_REQUEST),
    AI_MAKE_BAD_RESPONSE("잘못된 형식의 ai 응답이 생성됐습니다.", HttpStatus.CONFLICT),
    ;

    private final String message;
    private final HttpStatus status;

    RecordExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
