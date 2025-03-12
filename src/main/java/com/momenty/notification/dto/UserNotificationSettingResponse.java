package com.momenty.notification.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notification.domain.UserNotificationSetting;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record UserNotificationSettingResponse(
        List<UserNotificationSettingDto> userNotificationSettingDtos
) {
    @JsonNaming(SnakeCaseStrategy.class)
    public record UserNotificationSettingDto(
            Integer notificationTypeId,
            String notificationType
    ) {}

    public static UserNotificationSettingResponse of(List<UserNotificationSetting> settings) {
        List<UserNotificationSettingDto> dtoList = settings.stream()
                .map(setting -> new UserNotificationSettingDto(
                        setting.getNotificationType().getId(),
                        setting.getNotificationType().getType()
                ))
                .toList();

        return new UserNotificationSettingResponse(dtoList);
    }
}
