package com.momenty.record.repository;

import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.RecordDetailOption;
import com.momenty.record.domain.RecordOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordDetailOptionRepository extends JpaRepository<RecordDetailOption, Integer> {
    List<RecordDetailOption> findByRecordDetail(RecordDetail recordDetail);

    List<RecordDetailOption> findAllByRecordOption(RecordOption recordOption);
}
