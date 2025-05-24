package com.momenty.location.service;

import com.momenty.location.domain.Location;
import com.momenty.location.dto.LocationAddRequest;
import com.momenty.location.repository.LocationRepository;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public void addLocation(LocationAddRequest locationAddRequest, Integer userId) {
        User user = userRepository.getById(userId);

        Location location = Location.builder()
                .latitude(locationAddRequest.latitude())
                .longitude(locationAddRequest.longitude())
                .user(user)
                .build();

        locationRepository.save(location);
    }

    public List<Location> getLocations(Integer userId, Integer year, Integer month, Integer day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        LocalDateTime endDate = targetDate.atTime(LocalTime.MAX);
        LocalDateTime startDate = targetDate.atTime(LocalTime.MIN);

        User user = userRepository.getById(userId);

        return locationRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(user, startDate, endDate);
    }
}
