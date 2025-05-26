package com.momenty.record.repository;

import com.momenty.record.domain.RecordOption;
import com.momenty.record.domain.UserRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordOptionRepository extends JpaRepository<RecordOption, Integer> {

    List<RecordOption> findAllByRecord(UserRecord record);
}
