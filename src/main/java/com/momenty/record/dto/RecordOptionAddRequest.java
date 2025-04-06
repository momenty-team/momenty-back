package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordOptionAddRequest(
        @Size(min = 1, message = "옵션 리스트는 최소 하나 이상이어야 합니다.")
        List<String> options
) {

}
