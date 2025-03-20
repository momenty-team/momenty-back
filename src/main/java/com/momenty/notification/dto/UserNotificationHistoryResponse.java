package com.momenty.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notification.domain.UserNotificationHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record UserNotificationHistoryResponse(
        @Schema(description = "사용자의 알림 기록", required = true)
        List<UserNotificationHistoryDto> userNotificationHistories
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record UserNotificationHistoryDto(
            @Schema(description = "알림기록 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "알림 제목", example = "찬구신청", required = true)
            String title,

            @Schema(description = "알림 내용", example = "길동님에게 친구신청이 왔습니다.", required = true)
            String content,

            @Schema(description = "알림 icon url", example = "urlurl", required = false)
            String iconUrl,

            @Schema(description = "알림 읽음 여부", example = "false", required = true)
            Boolean isRead,

            @Schema(description = "알림 url", example = "urlurl", required = true)
            String url,

            @Schema(description = "알림 발송 날짜", example = "2024-03-20 12:00:00", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt,

            @Schema(description = "알림 수정 날짜", example = "2024-03-20 12:00:00", required = true)
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
                        history.getUrl(),
                        history.getCreatedAt(),
                        history.getUpdatedAt()
                ))
                .toList();

        return new UserNotificationHistoryResponse(dtoList);
    }
}
