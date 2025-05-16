    package com.momenty.record.controller;

    import com.momenty.global.annotation.UserId;
    import com.momenty.record.domain.RecordOption;
    import com.momenty.record.domain.RecordTrendSummary;
    import com.momenty.record.domain.RecordUnit;
    import com.momenty.record.domain.UserRecord;
    import com.momenty.record.dto.NumberTypeRecordTrend;
    import com.momenty.record.dto.OXTypeRecordTrend;
    import com.momenty.record.dto.OptionTypeRecordTrend;
    import com.momenty.record.dto.RecordAddRequest;
    import com.momenty.record.dto.RecordAnalysisResponse;
    import com.momenty.record.dto.RecordDetailAddRequest;
    import com.momenty.record.dto.RecordDetailDto;
    import com.momenty.record.dto.RecordDetailResponse;
    import com.momenty.record.dto.RecordDetailUpdateRequest;
    import com.momenty.record.dto.RecordDetailsResponse;
    import com.momenty.record.dto.RecordOptionAddRequest;
    import com.momenty.record.dto.RecordOptionUpdateRequest;
    import com.momenty.record.dto.RecordOptionsResponse;
    import com.momenty.record.dto.RecordTrendSummaryResponse;
    import com.momenty.record.dto.RecordUnitAddRequest;
    import com.momenty.record.dto.RecordUnitResponse;
    import com.momenty.record.dto.RecordUpdateRequest;
    import com.momenty.record.dto.RecordsResponse;
    import com.momenty.record.dto.TextTypeRecordTrend;
    import com.momenty.record.service.RecordService;
    import io.swagger.v3.oas.annotations.Parameter;
    import java.util.List;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.DeleteMapping;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    import reactor.core.publisher.Mono;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/records")
    public class RecordController {

        private final RecordService recordService;

        @PostMapping
        public ResponseEntity<Void> addRecord(
                @RequestBody RecordAddRequest recordAddRequest,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.addRecord(recordAddRequest, userId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        @GetMapping
        public ResponseEntity<RecordsResponse> getRecords(
                @Parameter(hidden = true) @UserId Integer userId,
                @RequestParam(required = false) Integer year,
                @RequestParam(required = false) Integer month,
                @RequestParam(required = false) Integer day
        ) {
            List<UserRecord> userRecords = recordService.getRecords(userId, year, month, day);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(RecordsResponse.of(userRecords));
        }

        @PostMapping("/{record_id}/details")
        public ResponseEntity<Void> addRecordDetail(
                @PathVariable("record_id") Integer recordId,
                @RequestBody RecordDetailAddRequest recordDetailAddRequest,
                @Parameter(hidden = true) @UserId Integer userId
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
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            List<RecordDetailDto> recordDetails = recordService.getRecordDetails(recordId, year, month, day);
            UserRecord record = recordService.getRecord(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(RecordDetailsResponse.of(record, recordDetails));
        }

        @GetMapping("/{record_id}/details/{detail_id}")
        public ResponseEntity<RecordDetailResponse> getRecordDetail(
                @PathVariable("record_id") Integer recordId,
                @PathVariable("detail_id") Integer detailId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            RecordDetailDto recordDetail = recordService.getRecordDetail(recordId, detailId);
            UserRecord record = recordService.getRecord(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(RecordDetailResponse.of(record, recordDetail));
        }

        @GetMapping("/{record_id}/options")
        public ResponseEntity<RecordOptionsResponse> getRecordOptions(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            List<RecordOption> recordOptions = recordService.getRecordOptions(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(RecordOptionsResponse.of(recordOptions));
        }

        @GetMapping("/{record_id}/unit")
        public ResponseEntity<RecordUnitResponse> getRecordUnit(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            RecordUnit recordUnit = recordService.getRecordUnit(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(RecordUnitResponse.of(recordUnit.getUnit()));
        }

        @PostMapping("/{record_id}/options")
        public ResponseEntity<Void> addRecordOption(
                @PathVariable("record_id") Integer recordId,
                @RequestBody RecordOptionAddRequest recordOptionAddRequest,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.addRecordOption(recordOptionAddRequest, recordId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        @PostMapping("/{record_id}/unit")
        public ResponseEntity<Void> addRecordUnit(
                @PathVariable("record_id") Integer recordId,
                @RequestBody RecordUnitAddRequest recordUnitAddRequest,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.addRecordUnit(recordUnitAddRequest, recordId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        @PutMapping("/{record_id}/unit")
        public ResponseEntity<Void> updateRecordUnit(
                @PathVariable("record_id") Integer recordId,
                @RequestBody RecordUnitAddRequest recordUnitAddRequest,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.addRecordUnit(recordUnitAddRequest, recordId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        @PutMapping("/{record_id}/options/{option_id}")
        public ResponseEntity<Void> updateRecordOption(
                @PathVariable("record_id") Integer recordId,
                @PathVariable("option_id") Integer optionId,
                @RequestBody RecordOptionUpdateRequest recordOptionUpdateRequest,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.updateRecordOption(recordOptionUpdateRequest, recordId, optionId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        @PutMapping("/{record_id}")
        public ResponseEntity<Void> updateRecord(
                @PathVariable("record_id") Integer recordId,
                @RequestBody RecordUpdateRequest recordUpdateRequest,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.updateRecord(recordUpdateRequest, recordId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        @PutMapping("/{record_id}/details/{detail_id}")
        public ResponseEntity<Void> updateRecordDetail(
                @PathVariable("record_id") Integer recordId,
                @PathVariable("detail_id") Integer detailId,
                @RequestBody RecordDetailUpdateRequest recordDetailUpdateRequest,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.updateRecordDetail(recordDetailUpdateRequest, detailId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        @DeleteMapping("/{record_id}")
        public ResponseEntity<Void> deleteRecord(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.deleteRecord(recordId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        @DeleteMapping("/{record_id}/details/{detail_id}")
        public ResponseEntity<Void> deleteRecord(
                @PathVariable("record_id") Integer recordId,
                @PathVariable("detail_id") Integer detailId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.deleteRecordDetail(detailId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        @DeleteMapping("/{record_id}/options/{option_id}")
        public ResponseEntity<Void> deleteRecordOption(
                @PathVariable("record_id") Integer recordId,
                @PathVariable("option_id") Integer optionId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            recordService.deleteRecordOption(optionId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        @GetMapping("/{record_id}/analysis")
        public Mono<ResponseEntity<RecordAnalysisResponse>> analyzeRecord(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            return recordService.analyzeRecord(recordId)
                    .map(ResponseEntity::ok);
        }

        @GetMapping("/analysis")
        public Mono<ResponseEntity<RecordAnalysisResponse>> analyzeRecords(
                @RequestParam(required = true) String period,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            return recordService.analyzeRecords(period, userId)
                    .map(ResponseEntity::ok);
        }

        @GetMapping("/{record_id}/trends/numbers")
        public ResponseEntity<NumberTypeRecordTrend> getNumberTypeRecordTrend(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            NumberTypeRecordTrend trend = recordService.getNumberTypeRecordTrend(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(trend);
        }

        @GetMapping("/{record_id}/trends/ox")
        public ResponseEntity<OXTypeRecordTrend> getOXTypeRecordTrend(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            OXTypeRecordTrend trend = recordService.getOXTypeRecordTrend(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(trend);
        }

        @GetMapping("/{record_id}/trends/options")
        public ResponseEntity<OptionTypeRecordTrend> getOptionTypeRecordTrend(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            OptionTypeRecordTrend trend = recordService.getOptionTypeRecordTrend(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(trend);
        }

        @GetMapping("/{record_id}/trends/texts")
        public ResponseEntity<TextTypeRecordTrend> getTextTypeRecordTrend(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            TextTypeRecordTrend trend = recordService.getTextTypeRecordTrend(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(trend);
        }

        @GetMapping("/{record_id}/trends/summary")
        public ResponseEntity<RecordTrendSummaryResponse> getTrendSummary(
                @PathVariable("record_id") Integer recordId,
                @Parameter(hidden = true) @UserId Integer userId
        ) {
            RecordTrendSummary trendSummary = recordService.getTrendSummary(recordId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(RecordTrendSummaryResponse.of(trendSummary.getContent()));
        }
    }
