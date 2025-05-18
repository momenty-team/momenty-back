package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record NumberTypeRecordTrend(
        @Schema(description = "시작 날짜", example = "2025-04-01", required = true)
        LocalDate startDate,

        @Schema(description = "종료 날짜", example = "2025-04-20", required = true)
        LocalDate endDate,

        @Schema(description = "데이터 리스트", required = true)
        List<Data> data,

        @Schema(description = "총 작성 횟수", example = "27", required = true)
        Integer totalCount,

        @Schema(description = "평균 작성 횟수", example = "3", required = true)
        Integer averageCount
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record Data(
            @Schema(description = "날짜", example = "2025-04-01", required = true)
            LocalDate date,

            @Schema(description = "요일", example = "월", required = true)
            String week,

            @Schema(description = "작성 횟수", example = "10", required = true)
            String value
    ) {}

    public static NumberTypeRecordTrend of(
            LocalDate startDate,
            LocalDate endDate,
            List<Data> data,
            Integer totalCount,
            Integer averageCount
    ) {
        return new NumberTypeRecordTrend(startDate, endDate, data, totalCount, averageCount);
    }
}
