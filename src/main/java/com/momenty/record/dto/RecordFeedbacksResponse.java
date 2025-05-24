package com.momenty.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.record.domain.RecordFeedback;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordFeedbacksResponse(
        @Schema(description = "기록 피드백 목록", required = true)
        List<RecordFeedbacksDto> recordFeedbacks
) {
    @JsonNaming(SnakeCaseStrategy.class)
    public record RecordFeedbacksDto (
            @Schema(description = "피드백 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "피드백 제목", example = "다양한 경험이 있었던 일주일", required = true)
            String title,

            @Schema(description = "피드백 레벨", example = "6", required = true)
            String level,

            @Schema(description = "피드백 내용", required = true)
            String content,

            @Schema(description = "피드백 생성 날짜", example = "2024-03-20 12:00:00", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt
    ) {}

    public static RecordFeedbacksResponse of (List<RecordFeedback> recordFeedbacks) {
        List<RecordFeedbacksDto> recordFeedbacksDtos = recordFeedbacks.stream()
                .map(recordFeedback -> new RecordFeedbacksDto(
                        recordFeedback.getId(),
                        recordFeedback.getTitle(),
                        recordFeedback.getLevel(),
                        recordFeedback.getContent(),
                        recordFeedback.getCreatedAt()
                )).toList();
        return new RecordFeedbacksResponse(recordFeedbacksDtos);
    }
}
