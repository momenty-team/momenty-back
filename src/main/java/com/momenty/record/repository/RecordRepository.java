package com.momenty.record.repository;

import com.momenty.record.domain.UserRecord;
import org.springframework.data.repository.Repository;

public interface RecordRepository extends Repository<UserRecord, Integer> {

    void save(UserRecord userRecord);
}
