package com.momenty.record.dto;

import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.UserRecord;
import java.time.LocalDateTime;
import java.util.List;

public record RecordDetailDto(
        UserRecord record,
        Integer id,
        List<String> content,
        Boolean isPublic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static RecordDetailDto of (RecordDetail recordDetails, List<String> content) {
        return new RecordDetailDto(
                recordDetails.getRecord(),
                recordDetails.getId(),
                content,
                recordDetails.getIsPublic(),
                recordDetails.getCreatedAt(),
                recordDetails.getUpdatedAt()
        );
    }
}
