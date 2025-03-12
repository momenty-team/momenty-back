package com.momenty.notification.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(SnakeCaseStrategy.class)
public record UpdateUserNotificationSettingRequest(
        @NotNull(message = "notification_type_id는 필수입니다.")
        Integer notificationTypeId,

        @NotNull(message = "is_enabled는 필수입니다.")
        Boolean isEnabled
) {

}
