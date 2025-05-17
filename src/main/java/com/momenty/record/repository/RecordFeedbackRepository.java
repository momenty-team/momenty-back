package com.momenty.record.repository;

import com.momenty.record.domain.RecordFeedback;
import com.momenty.user.domain.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface RecordFeedbackRepository extends Repository<RecordFeedback, Integer> {

    Optional<RecordFeedback> findFirstByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
            User user, LocalDateTime startDate, LocalDateTime endDate
    );

    RecordFeedback save(RecordFeedback recordFeedback);
}
