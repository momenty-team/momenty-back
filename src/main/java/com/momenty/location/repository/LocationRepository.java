package com.momenty.location.repository;

import com.momenty.location.domain.Location;
import com.momenty.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.Repository;

public interface LocationRepository extends Repository<Location, Integer> {

    void save(Location location);

    List<Location> findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
            User user, LocalDateTime startDate, LocalDateTime endDate
    );
}
