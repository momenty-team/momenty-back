package com.momenty.character.dto;

import lombok.Getter;

@Getter
public enum CharacterPromptMessage {
    USER_DATA_INFO(
            "아래에 HealthKit 정보, 사용자의 기록 데이터들 정보를 제공. 해당 정보들을 이용해 사용자에게 가장 어울리는 상태를 보여줄것.\n\n"
    ),
    CHOOSE_STATUS("위의 내용 바탕으로 아래의 상태 선택지 중 가장 어울리는 선택지를 정확하게 딱 한개만 골라 그것만 말할것. 그외의 말은 절대 하지 말것.\n"),
    CHARACTER_STATUS_OPTIONS(
            """
                    Healthy
                    Energy
                    Peaceful
                    Good rest
                    Soso
                    Stretching
                    Rest
                    Little tired
                    Tired
                    Stress
                    Lazy
                    Lethargy
            """
    ),
    ;

    private final String message;

    CharacterPromptMessage(String message) {
        this.message = message;
    }
}
