package com.momenty.notification.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notification.domain.UserNotificationHistory;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record UserNotificationHistoryResponse(
        List<UserNotificationHistoryDto> userNotificationHistories
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record UserNotificationHistoryDto(
            String title,
            String content,
            Boolean isRead
    ) {}

    public static UserNotificationHistoryResponse from(List<UserNotificationHistory> histories) {
        List<UserNotificationHistoryDto> dtoList = histories.stream()
                .map(history -> new UserNotificationHistoryDto(
                        history.getNotification().getTitle(),
                        history.getNotification().getContent(),
                        history.getIsRead()
                ))
                .toList();

        return new UserNotificationHistoryResponse(dtoList);
    }
}
