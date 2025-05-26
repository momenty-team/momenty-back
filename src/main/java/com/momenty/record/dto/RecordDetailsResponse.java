package com.momenty.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.record.domain.UserRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordDetailsResponse(
        @Schema(description = "기록 id", required = true)
        Integer id,

        @Schema(description = "기록 주제", required = true)
        String title,

        @Schema(description = "기록 방식", required = true)
        String method,

        @Schema(description = "기록 공개 여부", required = true)
        Boolean isPublic,

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
            Boolean isPublic,

            @Schema(description = "기록 디테일 생성 날짜", example = "2024-03-20 12:00:00", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt,

            @Schema(description = "기록 디테일 수정 날짜", example = "2024-03-20 12:00:00", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime updatedAt
    ) {}

    public static RecordDetailsResponse of (List<RecordDetailDto> recordDetailDtos) {
        UserRecord record = null;
        if (!recordDetailDtos.isEmpty()) {
            record = recordDetailDtos.get(0).record();
        }
        List<RecordDetails> recordDetails = recordDetailDtos.stream()
                .map(recordDetail -> new RecordDetails(
                        recordDetail.id(),
                        recordDetail.content(),
                        recordDetail.isPublic(),
                        recordDetail.createdAt(),
                        recordDetail.updatedAt()
                )).toList();

        if (record != null) {
            return new RecordDetailsResponse(
                    record.getId(), record.getTitle(), record.getMethod().name(), record.isPublic(), recordDetails
            );
        }
        return new RecordDetailsResponse(null, null, null, null, recordDetails);
    }
}
