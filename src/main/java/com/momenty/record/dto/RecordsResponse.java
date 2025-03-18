package com.momenty.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.momenty.record.domain.UserRecord;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record RecordsResponse(
        List<Records> records
) {
    public record Records(
            Integer id,
            String title,
            String method
    ) {}

    public static RecordsResponse of (List<UserRecord> userRecords) {
        List<Records> recordsList = userRecords.stream()
                .map(userRecord -> new Records(
                        userRecord.getId(),
                        userRecord.getTitle(),
                        userRecord.getMethod().name().toLowerCase()
                )).toList();
        return new RecordsResponse(recordsList);
    }
}
