package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordTrendSummaryResponse(
        @Schema(description = "요약", required = true)
        String summary
) {

    public static RecordTrendSummaryResponse of(String content) {
        return new RecordTrendSummaryResponse(content);
    }
}
