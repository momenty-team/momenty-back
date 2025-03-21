package com.momenty.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record UserStartDayResponse(
        @Schema(description = "사용자 생성 날짜", example = "2024-03-20 12:00:00", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {

    public static UserStartDayResponse from(LocalDateTime startDay) {
        return new UserStartDayResponse(startDay);
    }
}
