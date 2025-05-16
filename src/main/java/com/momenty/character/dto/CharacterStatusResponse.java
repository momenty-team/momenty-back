package com.momenty.character.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CharacterStatusResponse(
        @Schema(description = "기록 분석 결과", example = "기분이 점점 좋아졌습니다.", required = true)
        String result
) {

    public static CharacterStatusResponse from(String result) {
        return new CharacterStatusResponse(
                result
        );
    }
}