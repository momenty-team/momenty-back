package com.momenty.record.repository;

import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.UserRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.Repository;

public interface RecordDetailRepository extends Repository<RecordDetail, Integer> {

    void save(RecordDetail recordDetail);

    List<RecordDetail> findAllByRecord(UserRecord record);

    List<RecordDetail> findByRecordAndCreatedAtBetween(
            UserRecord userRecord,
            LocalDateTime startDate, LocalDateTime endDate
    );
}
