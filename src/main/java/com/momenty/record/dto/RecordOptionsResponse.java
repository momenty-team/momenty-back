package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.record.domain.RecordOption;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordOptionsResponse(
        List<RecordOptionsDto> options
) {
    public record RecordOptionsDto (
            Integer id,
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
