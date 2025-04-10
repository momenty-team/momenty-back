package com.momenty.record.repository;

import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.RecordDetailOption;
import com.momenty.record.domain.RecordOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RecordDetailOptionRepository extends JpaRepository<RecordDetailOption, Integer> {
    List<RecordDetailOption> findByRecordDetail(RecordDetail recordDetail);

    List<RecordDetailOption> findAllByRecordOption(RecordOption recordOption);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecordDetailOption rdo WHERE rdo.recordDetail = :recordDetail")
    void deleteAllByRecordDetail(@Param("recordDetail") RecordDetail recordDetail);

}
