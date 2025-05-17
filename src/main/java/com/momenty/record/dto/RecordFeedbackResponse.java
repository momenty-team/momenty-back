package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.record.domain.RecordFeedback;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordFeedbackResponse(
        @Schema(description = "기록 피드백 제목")
        String title,
        @Schema(description = "기록 피드백 레벨")
        String level,
        @Schema(description = "기록 피드백 피드백")
        String feedback

) {

    public static RecordFeedbackResponse from(RecordFeedback recordFeedback) {
        return new RecordFeedbackResponse(
                recordFeedback.getTitle(),
                recordFeedback.getLevel(),
                recordFeedback.getContent()
        );
    }
}
