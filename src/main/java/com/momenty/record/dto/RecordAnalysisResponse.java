package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordAnalysisResponse(
        @Schema(description = "기록 분석 결과", example = "기분이 점점 좋아졌습니다.", required = true)
        String result
) {

    public static RecordAnalysisResponse from (String result) {
        return new RecordAnalysisResponse(
                result
        );
    }
}
