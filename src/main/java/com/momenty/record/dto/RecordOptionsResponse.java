package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.record.domain.RecordOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordOptionsResponse(
        @Schema(description = "기록 옵션 목록", required = true)
        List<RecordOptionsDto> options
) {
    public record RecordOptionsDto (
            @Schema(description = "옵션 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "옵션 내용", example = "100mL", required = true)
            String option
    ) {}

    public static RecordOptionsResponse of (List<RecordOption> recordOptions) {
        List<RecordOptionsDto> recordOptionsDtos = recordOptions.stream()
                .map(recordOption -> new RecordOptionsDto(
                        recordOption.getId(),
                        recordOption.getOption()
                )).toList();
        return new RecordOptionsResponse(recordOptionsDtos);
    }
}
