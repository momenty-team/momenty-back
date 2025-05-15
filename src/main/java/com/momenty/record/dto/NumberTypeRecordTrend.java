package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(SnakeCaseStrategy.class)
public record NumberTypeRecordTrend(
        @Schema(description = "요일별 작성 횟수", required = true)
        DayCounts dayCounts,

        @Schema(description = "총 작성 횟수", example = "27", required = true)
        Integer totalCount,

        @Schema(description = "평균 작성 횟수", example = "3", required = true)
        Integer averageCount
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record DayCounts(
            @Schema(description = "월요일에 기록한 횟수", example = "1", required = true)
            Integer monday,

            @Schema(description = "화요일에 기록한 횟수", example = "1", required = true)
            Integer tuesday,

            @Schema(description = "수요일에 기록한 횟수", example = "1", required = true)
            Integer wednesday,

            @Schema(description = "목요일에 기록한 횟수", example = "1", required = true)
            Integer thursday,

            @Schema(description = "금요일에 기록한 횟수", example = "1", required = true)
            Integer friday,

            @Schema(description = "토요일에 기록한 횟수", example = "1", required = true)
            Integer saturday,

            @Schema(description = "일요일에 기록한 횟수", example = "1", required = true)
            Integer sunday
    ) {}

    public static NumberTypeRecordTrend of(
            Integer monday, Integer tuesday, Integer wednesday,
            Integer thursday, Integer friday, Integer saturday, Integer sunday,
            Integer totalCount,
            Integer averageCount
    ) {
        DayCounts dayCounts = new DayCounts(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        return new NumberTypeRecordTrend(dayCounts, totalCount, averageCount);
    }
}
