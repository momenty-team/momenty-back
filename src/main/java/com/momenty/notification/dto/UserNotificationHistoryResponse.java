package com.momenty.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notification.domain.UserNotificationHistory;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record UserNotificationHistoryResponse(
        List<UserNotificationHistoryDto> userNotificationHistories
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record UserNotificationHistoryDto(
            Integer id,
            String title,
            String content,
            String iconUrl,
            Boolean isRead,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime updatedAt
    ) {}

    public static UserNotificationHistoryResponse of(List<UserNotificationHistory> histories) {
        List<UserNotificationHistoryDto> dtoList = histories.stream()
                .map(history -> new UserNotificationHistoryDto(
                        history.getId(),
                        history.getTitle(),
                        history.getContent(),
                        history.getIconUrl(),
                        history.getIsRead(),
                        history.getCreatedAt(),
                        history.getUpdatedAt()
                ))
                .toList();

        return new UserNotificationHistoryResponse(dtoList);
    }
}
