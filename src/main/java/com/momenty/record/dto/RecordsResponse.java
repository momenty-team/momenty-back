package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.record.domain.UserRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordsResponse(
        @Schema(description = "기록 목록", required = true)
        List<Records> records
) {
    @JsonNaming(SnakeCaseStrategy.class)
    public record Records(
            @Schema(description = "기록 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "기록 주제", example = "물 마시기", required = true)
            String title,

            @Schema(description = "기록 방식", example = "option_type", required = true)
            String method,

            @Schema(description = "기록 공개 여부", example = "true", required = true)
            boolean isPublic
    ) {}

    public static RecordsResponse of (List<UserRecord> userRecords) {
        List<Records> recordsList = userRecords.stream()
                .map(userRecord -> new Records(
                        userRecord.getId(),
                        userRecord.getTitle(),
                        userRecord.getMethod().name().toLowerCase(),
                        userRecord.isPublic()
                )).toList();
        return new RecordsResponse(recordsList);
    }
}
