package com.momenty.notification.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record NotificationsResponse(
        @Schema(description = "알림 목록", required = true)
        List<NotificationsDto> notifications
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record NotificationsDto(
            @Schema(description = "알림 id", example = "1", required = true)
            Integer id,

            @Schema(description = "알림 제목", example = "찬구신청", required = true)
            String title,

            @Schema(description = "알림 내용", example = "길동님에게 친구신청이 왔습니다.", required = true)
            String content,

            @Schema(description = "알림 icon url", example = "urlurl", required = false)
            String iconUrl
    ) {}

    public static NotificationsResponse of(List<Notification> notifications) {
        List<NotificationsDto> dtoList = notifications.stream()
                .map(notification -> new NotificationsDto(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getContent(),
                        notification.getIconUrl()
                ))
                .toList();

        return new NotificationsResponse(dtoList);
    }
}
