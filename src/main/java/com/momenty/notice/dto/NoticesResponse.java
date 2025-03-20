package com.momenty.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record NoticesResponse(
        @Schema(description = "공지사항 목록", required = true)
        List<NoticeDto> notices
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record NoticeDto(
            @Schema(description = "공지사항 ID", example = "1", required = true)
            Integer id,

            @Schema(description = "공지사항 제목", example = "공지사항 제목입니다", required = true)
            String title,

            @Schema(description = "공지사항 내용", example = "공지사항 내용입니다", required = true)
            String content,

            @Schema(description = "공지 생성 날짜", example = "2024-03-20 12:00:00", required = true)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt,

            @Schema(description = "공지 수정 날짜", example = "2024-03-20 12:00:00", required = true)
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
