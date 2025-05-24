package com.momenty.location.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.location.dto.LocationAddRequest;
import com.momenty.location.service.LocationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
