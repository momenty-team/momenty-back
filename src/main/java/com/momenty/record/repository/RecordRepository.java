package com.momenty.record.repository;

import com.momenty.record.domain.UserRecord;
import com.momenty.user.domain.User;
import java.util.List;
import org.springframework.data.repository.Repository;

public interface RecordRepository extends Repository<UserRecord, Integer> {

    void save(UserRecord userRecord);

    List<UserRecord> getAllByUser(User user);
}
