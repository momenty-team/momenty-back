package com.momenty.record.repository;

import com.momenty.record.domain.RecordUnit;
import org.springframework.data.repository.Repository;

public interface RecordUnitRepository extends Repository<RecordUnit, Integer> {

    void save(RecordUnit recordUnit);
}
