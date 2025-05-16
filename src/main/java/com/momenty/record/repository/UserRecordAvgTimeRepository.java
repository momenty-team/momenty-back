package com.momenty.record.repository;

import com.momenty.record.domain.UserRecord;
import com.momenty.record.domain.UserRecordAvgTime;
import com.momenty.user.domain.User;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UserRecordAvgTimeRepository extends Repository<UserRecordAvgTime, Integer> {

    Optional<UserRecordAvgTime> findByUserAndRecord(User user, UserRecord record);

    void save(UserRecordAvgTime avgTime);
}
