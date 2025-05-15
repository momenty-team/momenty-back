package com.momenty.record.domain;

import lombok.Getter;

@Getter
public enum RecordAnalysisMessage {
    DATE_PATTERN("yyyy년 MM월 dd일 HH시 mm분"),
    DATE_AND_CONTENT_SEPARATOR(": "),
    CONTENT_AND_PROMPT("\\n"),
    PROMPT("다음은 사용자의 기록입니다.\\n 기록의 주제와 해당 주제의 기록들, 기록들의 시간을 바탕으로 아래 상태 중 사용자와 가장 잘 어울리는 하나를 골라주세요. 다른 부가 설명이나 말은 절대 하지 말고 반드시 상태 목록 중 하나만 반환해주세요\\n\\n"),
    RECORD_CONTENT("기록 내용:"),
    TREND_PROMPT(
            "데이터를 분석하여 주제에 따라 사용자에게 자연스럽게 설명해 주세요. 다음과 같은 지침을 참고하여 해석을 작성해 주세요:\n\n" +
                    "- 시간대(기상 시각, 취침 시각 등)일 경우:\n" +
                    "  - 기상 시간이 늦어지는 경우, 수면 패턴이 불규칙해질 가능성을 언급하고, 여유 시간이 줄어드는 경향을 설명하세요.\n" +
                    "  - 기상 시간이 더 이른 시간으로 이동한 경우, 규칙적인 생활 패턴이 형성되고 있는 긍정적인 변화를 강조하세요.\n\n" +
                    "- 횟수(운동 횟수, 공부 횟수 등)일 경우:\n" +
                    "  - 횟수가 증가하면, 목표 달성에 한 걸음 더 다가가고 있다는 점을 강조하세요.\n" +
                    "  - 횟수가 감소하면, 이전보다 활동량이 줄어들었다는 점을 언급하세요.\n\n" +
                    "- 양(섭취량, 소비량 등)일 경우:\n" +
                    "  - 변화가 있을 경우, 증가 또는 감소의 원인을 분석하고, 그에 따른 영향을 중립적인 어조로 설명하세요.\n\n" +
                    "위 지침에 따라 주제에 맞는 해석을 2~3줄의 자연스러운 문장으로 작성해 주세요."
    ),
    ;

    private final String message;

    RecordAnalysisMessage(String message) {
        this.message = message;
    }
}
