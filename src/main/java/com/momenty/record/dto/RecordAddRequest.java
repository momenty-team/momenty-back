package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordAddRequest(
        @NotBlank(message = "주제는 필수입니다.")
        String title,

        @NotBlank(message = "방식은 필수입니다.")
        String method,

        List<String> option,

        String unit
) {

}
