package com.momenty.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordDetailsResponse(
        List<RecordDetails> records
) {
    public record RecordDetails (
            Integer id,
            List<String> content,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime createdAt,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime updatedAt
    ) {}

    public static RecordDetailsResponse of (List<RecordDetailDto> recordDetailDtos) {
        List<RecordDetails> recordDetails = recordDetailDtos.stream()
                .map(recordDetail -> new RecordDetails(
                        recordDetail.id(),
                        recordDetail.content(),
                        recordDetail.createdAt(),
                        recordDetail.updatedAt()
                )).toList();
        return new RecordDetailsResponse(recordDetails);
    }
}
