package com.momenty.record.repository;

import com.momenty.record.domain.RecordTrendSummary;
import com.momenty.record.domain.UserRecord;
import com.momenty.user.domain.Gender;
import java.time.LocalDate;
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
    JOIN rts.record rec
    JOIN rec.user u
    WHERE u.id != :userId
    AND u.gender = :gender
    AND u.birthDate BETWEEN :minBirthDate AND :maxBirthDate
    ORDER BY rts.createdAt DESC
    """)
    List<RecordTrendSummary> findRecentSummariesFromSimilarUsers(
            @Param("userId") Integer userId,
            @Param("gender") Gender gender,
            @Param("minBirthDate") LocalDate minBirthDate,
            @Param("maxBirthDate") LocalDate maxBirthDate,
            Pageable pageable
    );

    void deleteByRecordAndCreatedAtBetween(UserRecord record, LocalDateTime startDate, LocalDateTime endDate);
}
