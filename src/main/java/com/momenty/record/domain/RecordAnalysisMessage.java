package com.momenty.record.domain;

import lombok.Getter;

@Getter
public enum RecordAnalysisMessage {
    DATE_PATTERN("yyyy년 MM월 dd일 HH시 mm분"),
    DATE_AND_CONTENT_SEPARATOR(": "),
    CONTENT_AND_PROMPT("\\n"),
    PROMPT("다음은 사용자의 기록입니다.\\n 기록의 주제와 해당 주제의 기록들, 기록들의 시간을 바탕으로 아래 상태 중 사용자와 가장 잘 어울리는 하나를 골라주세요. 다른 부가 설명이나 말은 절대 하지 말고 반드시 상태 목록 중 하나만 반환해주세요\\n\\n"),
    RECORD_CONTENT("기록 내용:"),
    ;

    private final String message;

    RecordAnalysisMessage(String message) {
        this.message = message;
    }
}
