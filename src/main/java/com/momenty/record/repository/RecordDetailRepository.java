package com.momenty.record.repository;

import static com.momenty.record.exception.RecordExceptionMessage.NOT_FOUND_RECORD_DETAIL;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.UserRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface RecordDetailRepository extends Repository<RecordDetail, Integer> {

    void save(RecordDetail recordDetail);

    List<RecordDetail> findAllByRecordOrderByCreatedAtDesc(UserRecord record);

    List<RecordDetail> findByRecordAndCreatedAtBetweenOrderByCreatedAtDesc(
            UserRecord userRecord,
            LocalDateTime startDate, LocalDateTime endDate
    );

    Optional<RecordDetail> findById(Integer id);

    default RecordDetail getById(Integer detailId) {
        return findById(detailId).orElseThrow(
                () -> new GlobalException(
                        NOT_FOUND_RECORD_DETAIL.getMessage(),
                        NOT_FOUND_RECORD_DETAIL.getStatus()
                )
        );
    }

    void deleteById(Integer detailId);

    List<RecordDetail> findByRecordInAndCreatedAtBetweenOrderByCreatedAtDesc(
            List<UserRecord> userRecords, LocalDateTime startDate, LocalDateTime endDate
    );

}
