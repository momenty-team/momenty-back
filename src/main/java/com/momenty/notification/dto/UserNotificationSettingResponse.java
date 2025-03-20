package com.momenty.notification.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notification.domain.UserNotificationSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record UserNotificationSettingResponse(
        @Schema(description = "사용자의 알림 설정", required = true)
        List<UserNotificationSettingDto> userNotificationSettingDtos
) {
    @JsonNaming(SnakeCaseStrategy.class)
    public record UserNotificationSettingDto(
            @Schema(description = "알림 타입 ID", example = "1", required = true)
            Integer notificationTypeId,

            @Schema(description = "알림 타입", example = "친구신청", required = true)
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
