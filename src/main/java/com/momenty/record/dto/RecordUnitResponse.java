package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordUnitResponse(
        @Schema(description = "기록 단위", required = true)
        String unit
) {

    public static RecordUnitResponse of (String unit) {
        return new RecordUnitResponse(unit);
    }
}
