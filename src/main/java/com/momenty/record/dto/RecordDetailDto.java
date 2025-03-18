package com.momenty.record.dto;

import com.momenty.record.domain.RecordDetail;
import java.time.LocalDateTime;
import java.util.List;

public record RecordDetailDto(
        Integer id,
        List<String> content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static RecordDetailDto of (RecordDetail recordDetails, List<String> content) {
        return new RecordDetailDto(
                recordDetails.getId(),
                content,
                recordDetails.getCreatedAt(),
                recordDetails.getUpdatedAt()
        );
    }
}
