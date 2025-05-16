package com.momenty.character.dto;

import lombok.Getter;

@Getter
public enum CharacterPromptMessage {
    USER_DATA_INFO(
            "아래에 HealthKit 정보와 사용자의 기록 데이터들을 주제별로 요약한 정보가 있습니다. 해당 정보들을 이용해 사용자에게 가장 어울리는 상태를 보여주려고 합니다.\n\n"
    ),
    CHOOSE_STATUS("위의 HealthKit와 사용자 기록 데이터를 바탕으로 아래의 상태 선택지 중에 가장 어울리는 선택지를 정확하게 딱 한개만 골라 그것만 말해주세요. 그외의 말은 절대 하지 말아주세요.\n"),
    CHARACTER_STATUS_OPTIONS(
            """
                    지쳤어
                    스트레스 받아
                    게을러
                    무기력해
                    과로했어
                    혼잡해
                    아파
                    졸려
                    슬퍼
                    회복 중이야
            """
    ),
    ;

    private final String message;

    CharacterPromptMessage(String message) {
        this.message = message;
    }
}
