package com.momenty.record.repository;

import static com.momenty.record.exception.RecordExceptionMessage.NOT_FOUND_RECORD_DETAIL;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.UserRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

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

    boolean existsByRecordIdAndCreatedAtBetween(Integer recordId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = """
    SELECT rd
    FROM RecordDetail rd
    JOIN FETCH rd.record r
    WHERE r.user.id = :userId
      AND rd.createdAt BETWEEN :startDate AND :endDate
    ORDER BY rd.createdAt DESC
    """)
    List<RecordDetail> findRecentDetails(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
