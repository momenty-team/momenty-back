package com.momenty.location.service;

import com.momenty.location.domain.Location;
import com.momenty.location.dto.LocationAddRequest;
import com.momenty.location.repository.LocationRepository;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
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
}
