package com.momenty.record.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.record.dto.RecordAddRequest;
import com.momenty.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping()
    public ResponseEntity<Void> addRecord(
            @RequestBody RecordAddRequest recordAddRequest,
            @UserId Integer userId
    ) {
        recordService.addRecord(recordAddRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
