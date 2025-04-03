package com.momenty.record.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecordAnalysisStatusMessage {
    ACTIVE_HAPPY("1. 활기차고 기분이 좋습니다."),
    CALM_STABLE("2. 평온하고 안정된 상태입니다."),
    PASSIONATE("3. 열정적이고 의욕이 넘칩니다."),
    MILD_STRESS("4. 스트레스를 약간 받고 있습니다."),
    ANXIOUS("5. 걱정이 많고 불안정한 상태입니다."),
    EXHAUSTED("6. 매우 지치고 피곤한 상태입니다."),
    DEPRESSED("7. 우울하거나 슬퍼 보입니다."),
    LONELY("8. 외로움을 느끼고 있습니다."),
    MOOD_SWINGS("9. 감정의 기복이 심합니다."),
    APATHETIC("10. 무기력하고 아무것도 하기 싫습니다.");

    private final String message;

    @Override
    public String toString() {
        return message;
    }
}
