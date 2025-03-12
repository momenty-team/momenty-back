package com.momenty.notification.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(SnakeCaseStrategy.class)
public record UserNotificationHistoryUpdateRequest(
        @NotNull(message = "history_id는 필수입니다.")
        Integer userNotificationHistoryId
) {

}
