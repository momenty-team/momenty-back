package com.momenty.record.repository;

import static com.momenty.record.exception.RecordExceptionMessage.*;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.UserRecord;
import com.momenty.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface RecordRepository extends Repository<UserRecord, Integer> {

    void save(UserRecord userRecord);

    List<UserRecord> getAllByUser(User user);

    List<UserRecord> findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
            User user,
            LocalDateTime startDate, LocalDateTime endDate
    );

    List<UserRecord> findAllByUserOrderByCreatedAtDesc(User user);

    Optional<UserRecord> findById(Integer id);

    default UserRecord getById( Integer id) {
        return findById(id).orElseThrow(
                () -> new GlobalException(
                        NOT_FOUND_RECORD.getMessage(),
                        NOT_FOUND_RECORD.getStatus()
                )
        );
    }

    void deleteById(Integer id);

    List<UserRecord> findAllByUser(User user);
}
