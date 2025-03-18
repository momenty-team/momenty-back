package com.momenty.record.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.UserRecord;
import com.momenty.record.dto.RecordAddRequest;
import com.momenty.record.dto.RecordDetailAddRequest;
import com.momenty.record.dto.RecordDetailDto;
import com.momenty.record.dto.RecordDetailResponse;
import com.momenty.record.dto.RecordDetailsResponse;
import com.momenty.record.dto.RecordsResponse;
import com.momenty.record.service.RecordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<Void> addRecord(
            @RequestBody RecordAddRequest recordAddRequest,
            @UserId Integer userId
    ) {
        recordService.addRecord(recordAddRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<RecordsResponse> getRecords(
            @UserId Integer userId
    ) {
        List<UserRecord> userRecords = recordService.getRecords(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RecordsResponse.of(userRecords));
    }

    @PostMapping("/{record_id}/details")
    public ResponseEntity<Void> addRecordDetail(
            @PathVariable("record_id") Integer recordId,
            @RequestBody RecordDetailAddRequest recordDetailAddRequest,
            @UserId Integer userId
    ) {
        recordService.addRecordDetail(recordId, recordDetailAddRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{record_id}/details")
    public ResponseEntity<RecordDetailsResponse> getRecordDetails(
            @PathVariable("record_id") Integer recordId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            @UserId Integer userId
    ) {
        List<RecordDetailDto> recordDetails = recordService.getRecordDetails(recordId, year, month, day);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RecordDetailsResponse.of(recordDetails));
    }

    @GetMapping("/{record_id}/details/{detail_id}")
    public ResponseEntity<RecordDetailResponse> getRecordDetail(
            @PathVariable("record_id") Integer recordId,
            @PathVariable("detail_id") Integer detailId,
            @UserId Integer userId
    ) {
        RecordDetailDto recordDetail = recordService.getRecordDetail(recordId, detailId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RecordDetailResponse.of(recordDetail));
    }
}
