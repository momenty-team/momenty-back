package com.momenty.record.repository;

import com.momenty.record.domain.RecordFeedback;
import com.momenty.record.domain.RecordTrendSummary;
import com.momenty.user.domain.Gender;
import com.momenty.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface RecordFeedbackRepository extends Repository<RecordFeedback, Integer> {

    Optional<RecordFeedback> findFirstByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
            User user, LocalDateTime startDate, LocalDateTime endDate
    );

    RecordFeedback save(RecordFeedback recordFeedback);

    @Query("""
    SELECT rf FROM RecordFeedback rf
    JOIN rf.user u
    WHERE u.id != :userId
      AND u.gender = :gender
      AND u.birthDate BETWEEN :minBirthDate AND :maxBirthDate
    ORDER BY rf.createdAt DESC
    """)
    List<RecordFeedback> findRecentFeedbackFromSimilarUsers(
            @Param("userId") Integer userId,
            @Param("gender") Gender gender,
            @Param("minBirthDate") LocalDate minBirthDate,
            @Param("maxBirthDate") LocalDate maxBirthDate,
            Pageable pageable
    );

}
