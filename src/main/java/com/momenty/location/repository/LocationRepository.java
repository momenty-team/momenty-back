package com.momenty.location.repository;

import com.momenty.location.domain.Location;
import org.springframework.data.repository.Repository;

public interface LocationRepository extends Repository<Location, Integer> {

    void save(Location location);
}
