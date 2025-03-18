package com.momenty.record.repository;

import com.momenty.record.domain.RecordDetailOption;
import org.springframework.data.repository.Repository;

public interface RecordDetailOptionRepository extends Repository<RecordDetailOption, Integer> {

    void save(RecordDetailOption recordDetailOption);
}
