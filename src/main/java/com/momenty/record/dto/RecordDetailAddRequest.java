package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordDetailAddRequest(
        String content,
        Integer optionId,

        @NotNull(message = "공개 여부는 필수입니다.")
        Boolean isPublic
) {

}
