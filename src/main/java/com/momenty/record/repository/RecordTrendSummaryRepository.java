package com.momenty.record.repository;

import com.momenty.record.domain.RecordTrendSummary;
import com.momenty.record.domain.UserRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface RecordTrendSummaryRepository extends Repository<RecordTrendSummary, Integer> {

    Optional<RecordTrendSummary> findFirstByRecordAndCreatedAtBetweenOrderByCreatedAtDesc(
            UserRecord record,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    RecordTrendSummary save(RecordTrendSummary recordTrendSummary);

    @Query("""
    SELECT rts FROM RecordTrendSummary rts
    WHERE rts.user.id != :userId
    ORDER BY rts.createdAt DESC
    """)
    List<RecordTrendSummary> findRecentSummariesFromOtherUsers(
            @Param("userId") Integer userId,
            Pageable pageable
    );

    void deleteByRecordAndCreatedAtBetween(UserRecord record, LocalDateTime startDate, LocalDateTime endDate);
}
