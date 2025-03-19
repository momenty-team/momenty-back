package com.momenty.record.repository;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.RecordDetailOption;
import com.momenty.record.domain.RecordOption;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface RecordDetailOptionRepository extends Repository<RecordDetailOption, Integer> {

    void save(RecordDetailOption recordDetailOption);

    List<RecordDetailOption> findByRecordDetail(RecordDetail recordDetail);

    List<RecordDetailOption> findAllByRecordOption(RecordOption recordOption);
}
