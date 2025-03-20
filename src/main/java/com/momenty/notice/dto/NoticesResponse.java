package com.momenty.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notice.domain.Notice;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record NoticesResponse(
        List<NoticeDto> notices
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record NoticeDto(
            Integer id,
            String title,
            String content,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime updatedAt
    ) {}

    public static NoticesResponse of(List<Notice> notices) {
        List<NoticeDto> dtoList = notices.stream()
                .map(notice -> new NoticeDto(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getCreatedAt(),
                        notice.getUpdatedAt()
                ))
                .toList();

        return new NoticesResponse(dtoList);
    }
}
