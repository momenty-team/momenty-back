package com.momenty.record.repository;

import com.momenty.record.domain.RecordTrendSummary;
import com.momenty.record.domain.UserRecord;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface RecordTrendSummaryRepository extends Repository<RecordTrendSummary, Integer> {

    Optional<RecordTrendSummary> findByRecordAndCreatedAtBetween(
            UserRecord record,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    RecordTrendSummary save(RecordTrendSummary recordTrendSummary);
}
