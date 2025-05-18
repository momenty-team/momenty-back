package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record OXTypeRecordTrend(
        @Schema(description = "시작 날짜", required = true)
        LocalDate startDate,

        @Schema(description = "종료 날짜", required = true)
        LocalDate endDate,

        @Schema(description = "OX 데이터 리스트", required = true)
        List<Data> data,

        @Schema(description = "총 OX 횟수", required = true)
        OXCount totalCounts,

        @Schema(description = "평균 기록 횟수", example = "3", required = true)
        Integer averageCount
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record Data(
            @Schema(description = "날짜", example = "2025-04-01", required = true)
            LocalDate date,

            @Schema(description = "요일", example = "월", required = true)
            String week,

            @Schema(description = "OX 횟수", required = true)
            OXCount oxCount
    ) {}

    @JsonNaming(SnakeCaseStrategy.class)
    public record OXCount(
            @Schema(description = "O 횟수", example = "5", required = true)
            Integer oCount,

            @Schema(description = "X 횟수", example = "3", required = true)
            Integer xCount
    ) {}

    public static OXTypeRecordTrend of(
            LocalDate startDate, LocalDate endDate,
            List<Data> data, OXCount totalCounts, Integer averageCount
    ) {
        return new OXTypeRecordTrend(startDate, endDate, data, totalCounts, averageCount);
    }
}
