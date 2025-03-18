package com.momenty.record.repository;

import static com.momenty.record.exception.RecordExceptionMessage.*;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.RecordUnit;
import com.momenty.record.domain.UserRecord;
import com.momenty.record.exception.RecordExceptionMessage;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface RecordUnitRepository extends Repository<RecordUnit, Integer> {

    void save(RecordUnit recordUnit);

    Optional<RecordUnit> findByUserRecord(UserRecord userRecord);

    default RecordUnit getByRecord(UserRecord record) {
        return findByUserRecord(record).orElseThrow(
                () -> new GlobalException(
                        NOT_FOUND_RECORD_UNIT.getMessage(),
                        NOT_FOUND_RECORD_UNIT.getStatus()
                )
        );
    }
}
