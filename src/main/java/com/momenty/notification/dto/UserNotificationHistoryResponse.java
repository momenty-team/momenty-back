package com.momenty.notification.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notification.domain.UserNotificationHistory;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record UserNotificationHistoryResponse(
        List<UserNotificationHistory> userNotificationHistories
) {

    public static UserNotificationHistoryResponse of(List<UserNotificationHistory> histories) {
        return new UserNotificationHistoryResponse(histories);
    }
}
