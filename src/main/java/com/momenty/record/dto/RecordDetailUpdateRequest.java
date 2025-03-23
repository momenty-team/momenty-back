package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordDetailUpdateRequest(
        String content,
        Boolean isPublic,
        Integer hour,
        Integer minute
) {

}
