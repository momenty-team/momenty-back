package com.momenty.character.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(SnakeCaseStrategy.class)
public record CharacterStatusRequest(
        @NotNull(message = "헬스키트 정보는 필수입니다.")
        String healthKit
) {

}
