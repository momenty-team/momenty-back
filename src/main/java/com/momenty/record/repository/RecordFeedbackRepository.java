package com.momenty.record.repository;

import com.momenty.record.domain.RecordFeedback;
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

    @Query(
            value = """
        SELECT rf.*
        FROM record_feedback rf
        JOIN user u ON rf.user_id = u.id
        INNER JOIN (
            SELECT user_id, MAX(created_at) AS max_created_at
            FROM record_feedback
            WHERE user_id != :userId
            GROUP BY user_id
        ) latest ON rf.user_id = latest.user_id AND rf.created_at = latest.max_created_at
        WHERE u.gender = :gender
          AND u.birth_date BETWEEN :minBirthDate AND :maxBirthDate
        ORDER BY rf.created_at DESC
        LIMIT 100
        """,
            nativeQuery = true
    )
    List<RecordFeedback> findRecentFeedbackFromSimilarUsers(
            @Param("userId") Integer userId,
            @Param("gender") String gender,
            @Param("minBirthDate") LocalDate minBirthDate,
            @Param("maxBirthDate") LocalDate maxBirthDate
    );


    List<RecordFeedback> findAllByUser(User user);
}
