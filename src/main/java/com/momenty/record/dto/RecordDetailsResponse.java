package com.momenty.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordDetailsResponse(
        @Schema(description = "기록 디테일 목록", required = true)
        List<RecordDetails> records
) {
    @JsonNaming(SnakeCaseStrategy.class)
    public record RecordDetails (
            @Schema(description = "기록 디테일 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "기록 디테일 내용", example = "오늘은 슬픈날이었다.", required = true)
            List<String> content,

            @Schema(description = "기록 디테일 공개 여부", example = "true", required = true)
            boolean isPublic,

            @Schema(description = "기록 디테일 생성 날짜", example = "2024-03-20 12:00:00", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt,

            @Schema(description = "기록 디테일 수정 날짜", example = "2024-03-20 12:00:00", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime updatedAt
    ) {}

    public static RecordDetailsResponse of (List<RecordDetailDto> recordDetailDtos) {
        List<RecordDetails> recordDetails = recordDetailDtos.stream()
                .map(recordDetail -> new RecordDetails(
                        recordDetail.id(),
                        recordDetail.content(),
                        recordDetail.isPublic(),
                        recordDetail.createdAt(),
                        recordDetail.updatedAt()
                )).toList();
        return new RecordDetailsResponse(recordDetails);
    }
}
