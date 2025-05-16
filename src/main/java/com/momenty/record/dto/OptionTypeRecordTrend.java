package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record OptionTypeRecordTrend(
        @Schema(description = "요일별 기록 데이터", required = true)
        DayCounts dayCounts,

        @Schema(description = "총 기록 횟수", example = "27", required = true)
        Integer totalCount,

        @Schema(description = "가장 많이 중복된 옵션", required = true)
        OptionDetail mostFrequentOption
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record DayCounts(
            @Schema(description = "월요일 기록 데이터", required = true)
            DayRecord monday,

            @Schema(description = "화요일 기록 데이터", required = true)
            DayRecord tuesday,

            @Schema(description = "수요일 기록 데이터", required = true)
            DayRecord wednesday,

            @Schema(description = "목요일 기록 데이터", required = true)
            DayRecord thursday,

            @Schema(description = "금요일 기록 데이터", required = true)
            DayRecord friday,

            @Schema(description = "토요일 기록 데이터", required = true)
            DayRecord saturday,

            @Schema(description = "일요일 기록 데이터", required = true)
            DayRecord sunday
    ) {}

    @JsonNaming(SnakeCaseStrategy.class)
    public record DayRecord(
            @Schema(description = "해당 요일의 기록 횟수", example = "3", required = true)
            Integer count,

            @Schema(description = "해당 요일의 옵션 목록", required = true)
            List<OptionDetail> options
    ) {}

    @JsonNaming(SnakeCaseStrategy.class)
    public record OptionDetail(
            @Schema(description = "옵션 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "옵션 값", example = "YES", required = true)
            String value
    ) {}

    public static OptionTypeRecordTrend of(
            DayRecord monday, DayRecord tuesday, DayRecord wednesday,
            DayRecord thursday, DayRecord friday, DayRecord saturday, DayRecord sunday,
            Integer totalCount,
            OptionDetail mostFrequentOption
    ) {
        DayCounts dayCounts = new DayCounts(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        return new OptionTypeRecordTrend(dayCounts, totalCount, mostFrequentOption);
    }
}
