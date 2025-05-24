package com.momenty.location.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.location.domain.Location;
import com.momenty.location.dto.LocationAddRequest;
import com.momenty.location.dto.LocationsResponse;
import com.momenty.location.service.LocationService;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<Void> addLocation(
        @RequestBody LocationAddRequest locationAddRequest,
        @Parameter(hidden = true) @UserId Integer userId
    ) {
        locationService.addLocation(locationAddRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<LocationsResponse> getLocations(
        @Parameter(hidden = true) @UserId Integer userId,
            @RequestParam(required = true) Integer year,
            @RequestParam(required = true) Integer month,
            @RequestParam(required = true) Integer day
    ) {
        List<Location> locations = locationService.getLocations(userId, year, month, day);
        return ResponseEntity.status(HttpStatus.OK)
                .body(LocationsResponse.of(locations));
    }
}
