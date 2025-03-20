package com.momenty.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record RecordDetailResponse(
        @Schema(description = "기록 디테일 ID", example = "1", required = true)
        Integer id,

        @Schema(description = "기록 디테일 내용", example = "오늘은 슬픈날이었다.", required = true)
        List<String> content,

        @Schema(description = "기록 디테일 생성 날짜", example = "2024-03-20 12:00:00", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,

        @Schema(description = "기록 디테일 수정 날짜", example = "2024-03-20 12:00:00", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime updatedAt
) {
    public static RecordDetailResponse of (RecordDetailDto recordDetailDto) {
        return new RecordDetailResponse(
                recordDetailDto.id(), recordDetailDto.content(),
                recordDetailDto.createdAt(), recordDetailDto.updatedAt()
        );
    }
}
