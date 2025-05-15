package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(SnakeCaseStrategy.class)
public record OXTypeRecordTrend(
        @Schema(description = "요일별 OX 횟수", required = true)
        DayCounts dayCounts,

        @Schema(description = "총 OX 횟수", required = true)
        OXCount totalCounts,

        @Schema(description = "평균 기록 횟수", example = "3", required = true)
        Integer averageCount
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record DayCounts(
            @Schema(description = "월요일에 기록한 OX 횟수", required = true)
            OXCount monday,

            @Schema(description = "화요일에 기록한 OX 횟수", required = true)
            OXCount tuesday,

            @Schema(description = "수요일에 기록한 OX 횟수", required = true)
            OXCount wednesday,

            @Schema(description = "목요일에 기록한 OX 횟수", required = true)
            OXCount thursday,

            @Schema(description = "금요일에 기록한 OX 횟수", required = true)
            OXCount friday,

            @Schema(description = "토요일에 기록한 OX 횟수", required = true)
            OXCount saturday,

            @Schema(description = "일요일에 기록한 OX 횟수", required = true)
            OXCount sunday
    ) {}

    @JsonNaming(SnakeCaseStrategy.class)
    public record OXCount(
            @Schema(description = "O 횟수", example = "5", required = true)
            Integer oCount,

            @Schema(description = "X 횟수", example = "3", required = true)
            Integer xCount
    ) {}

    public static OXTypeRecordTrend of(
            OXCount monday, OXCount tuesday, OXCount wednesday,
            OXCount thursday, OXCount friday, OXCount saturday, OXCount sunday,
            OXCount totalCounts,
            Integer averageCount
    ) {
        DayCounts dayCounts = new DayCounts(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        return new OXTypeRecordTrend(dayCounts, totalCounts, averageCount);
    }
}
