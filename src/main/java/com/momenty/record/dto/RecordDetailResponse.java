package com.momenty.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record RecordDetailResponse(
        Integer id,
        List<String> content,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,

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
