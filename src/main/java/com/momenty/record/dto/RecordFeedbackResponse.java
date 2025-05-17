package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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

    public static RecordFeedbackResponse from(String result) {
        String[] lines = result.split("\n");
        String title = "";
        String level = "";
        String feedback = "";

        for (String line : lines) {
            if (line.startsWith("title: ")) {
                title = line.substring(7).trim();
            } else if (line.startsWith("level: ")) {
                level = line.substring(7).trim();
            } else if (line.startsWith("feedback: ")) {
                feedback = line.substring(10).trim();
            }
        }

        return new RecordFeedbackResponse(title, level, feedback);
    }
}
