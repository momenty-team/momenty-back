package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record OptionTypeRecordTrend(
        @Schema(description = "시작 날짜", required = true)
        LocalDate startDate,

        @Schema(description = "종료 날짜", required = true)
        LocalDate endDate,

        @Schema(description = "옵션 데이터 리스트", required = true)
        List<Data> data,

        @Schema(description = "총 기록 횟수", example = "27", required = true)
        Integer totalCount,

        @Schema(description = "가장 많이 중복된 옵션", required = true)
        OptionDetail mostFrequentOption
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record Data(
            @Schema(description = "날짜", example = "2025-04-01", required = true)
            LocalDate date,

            @Schema(description = "요일", example = "월", required = true)
            String week,

            @Schema(description = "해당 날짜의 옵션 목록", required = true)
            List<OptionDetail> options,

            @Schema(description = "해당 날짜의 기록 횟수", example = "3", required = true)
            Integer count
    ) {}

    @JsonNaming(SnakeCaseStrategy.class)
    public record OptionDetail(
            @Schema(description = "옵션 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "옵션 값", example = "YES", required = true)
            String value
    ) {}

    public static OptionTypeRecordTrend of(
            LocalDate startDate, LocalDate endDate,
            List<Data> data, Integer totalCount, OptionDetail mostFrequentOption
    ) {
        return new OptionTypeRecordTrend(startDate, endDate, data, totalCount, mostFrequentOption);
    }
}
