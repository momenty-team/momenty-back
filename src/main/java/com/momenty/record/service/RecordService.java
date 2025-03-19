package com.momenty.record.service;

import static com.momenty.record.exception.RecordExceptionMessage.*;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.RecordDetailOption;
import com.momenty.record.domain.RecordMethod;
import com.momenty.record.domain.RecordOption;
import com.momenty.record.domain.RecordUnit;
import com.momenty.record.domain.UserRecord;
import com.momenty.record.dto.RecordAddRequest;
import com.momenty.record.dto.RecordDetailAddRequest;
import com.momenty.record.dto.RecordDetailDto;
import com.momenty.record.dto.RecordOptionAddRequest;
import com.momenty.record.dto.RecordOptionUpdateRequest;
import com.momenty.record.dto.RecordUnitAddRequest;
import com.momenty.record.dto.RecordUpdateRequest;
import com.momenty.record.repository.RecordDetailOptionRepository;
import com.momenty.record.repository.RecordDetailRepository;
import com.momenty.record.repository.RecordOptionRepository;
import com.momenty.record.repository.RecordRepository;
import com.momenty.record.repository.RecordUnitRepository;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import com.nimbusds.jose.util.Pair;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final RecordOptionRepository recordOptionRepository;
    private final RecordUnitRepository recordUnitRepository;
    private final RecordDetailOptionRepository recordDetailOptionRepository;
    private final RecordDetailRepository recordDetailRepository;

    @Transactional
    public void addRecord(RecordAddRequest recordAddRequest, Integer userId) {
        validMethodType(recordAddRequest.method());
        User user = userRepository.getById(userId);
        UserRecord userRecord = UserRecord.builder()
                .title(recordAddRequest.title())
                .method(RecordMethod.valueOf(recordAddRequest.method().toUpperCase()))
                .user(user)
                .build();

        recordRepository.save(userRecord);
        RecordMethod recordMethod = userRecord.getMethod();
        if (isNumberType(recordMethod)) {
            saveRecordUnit(recordAddRequest.unit(), user, userRecord);
        }
        if (isOptionType(recordMethod)) {
            saveRecordUnit(recordAddRequest.unit(), user, userRecord);
            saveRecordOptions(recordAddRequest.option(), user, userRecord);
        }
    }

    private void saveRecordOptions(List<String> options, User user, UserRecord userRecord) {
        List<RecordOption> recordOptions = options.stream()
                .map(option -> RecordOption.builder()
                        .option(option)
                        .user(user)
                        .userRecord(userRecord)
                        .build()
                ).toList();

        recordOptionRepository.saveAll(recordOptions);
    }

    private void saveRecordUnit(String unit, User user, UserRecord userRecord) {
        RecordUnit recordUnit = RecordUnit.builder()
                .unit(unit)
                .user(user)
                .userRecord(userRecord)
                .build();
        recordUnitRepository.save(recordUnit);
    }


    private boolean isNumberType(RecordMethod recordMethod) {
        return RecordMethod.NUMBER_TYPE.equals(recordMethod);
    }

    private boolean isOptionType(RecordMethod recordMethod) {
        return RecordMethod.OPTION_TYPE.equals(recordMethod);
    }

    private void validMethodType(String method) {
        try {
            RecordMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(BAD_RECORD_METHOD.getMessage(), BAD_RECORD_METHOD.getStatus());
        }
    }

    public List<UserRecord> getRecords(Integer userId) {
        User user = userRepository.getById(userId);
        return recordRepository.getAllByUser(user);
    }

    @Transactional
    public void addRecordDetail(Integer recordId, RecordDetailAddRequest recordDetailAddRequest) {
        UserRecord record = recordRepository.getById(recordId);
        RecordDetail recordDetail = RecordDetail.builder()
                .content(recordDetailAddRequest.content())
                .record(record)
                .build();
        recordDetailRepository.save(recordDetail);

        if (isOptionType(record.getMethod())) {
            RecordOption recordOption = getRecordOption(recordDetailAddRequest.optionId());

            RecordDetailOption recordDetailOption = RecordDetailOption.builder()
                    .recordDetail(recordDetail)
                    .recordOption(recordOption)
                    .build();

            recordDetailOptionRepository.save(recordDetailOption);
        }
    }

    private RecordOption getRecordOption(Integer recordOptionId) {
        return recordOptionRepository.findById(recordOptionId)
                .orElseThrow(
                        () -> new GlobalException(
                                NOT_FOUND_RECORD_OPTION.getMessage(),
                                NOT_FOUND_RECORD_OPTION.getStatus()
                        )
                );
    }

    public List<RecordDetailDto> getRecordDetails(Integer recordId, Integer year, Integer month, Integer day) {
        Pair<LocalDateTime, LocalDateTime> dateFilter = makeDateFilter(year, month, day);
        LocalDateTime startDate = dateFilter.getLeft();
        LocalDateTime endDate = dateFilter.getRight();

        UserRecord userRecord = recordRepository.getById(recordId);
        List<RecordDetail> recordDetails;
        if (startDate != null && endDate != null) {
            recordDetails = recordDetailRepository.findByRecordAndCreatedAtBetween(userRecord, startDate, endDate);
        } else {
            recordDetails = recordDetailRepository.findAllByRecord(userRecord);
        }

        boolean isOptionType = isOptionType(userRecord.getMethod());
        boolean isNumberType = isNumberType(userRecord.getMethod());

        RecordUnit recordUnit = (isOptionType || isNumberType)
                ? recordUnitRepository.getByRecord(userRecord)
                : null;

        return recordDetails.stream()
                .map(recordDetail -> getRecordDetailDto(recordDetail, isOptionType, isNumberType, recordUnit))
                .toList();
    }

    private Pair<LocalDateTime, LocalDateTime> makeDateFilter(Integer year, Integer month, Integer day) {
        if (year != null && month != null && day != null) {
            return Pair.of(
                    LocalDateTime.of(year, month, day, 0, 0, 0),
                    LocalDateTime.of(year, month, day, 23, 59, 59)
            );
        } else if (year != null && month != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            return Pair.of(
                    yearMonth.atDay(1).atStartOfDay(),
                    yearMonth.atEndOfMonth().atTime(23, 59, 59)
            );
        } else if (year != null) {
            return Pair.of(
                    LocalDateTime.of(year, 1, 1, 0, 0, 0),
                    LocalDateTime.of(year, 12, 31, 23, 59, 59)
            );
        }
        return Pair.of(null, null);
    }

    public RecordDetailDto getRecordDetail(Integer recordId, Integer detailId) {
        UserRecord userRecord = recordRepository.getById(recordId);
        RecordDetail recordDetail = recordDetailRepository.getById(detailId);

        boolean isOptionType = isOptionType(userRecord.getMethod());
        boolean isNumberType = isNumberType(userRecord.getMethod());

        RecordUnit recordUnit = (isOptionType || isNumberType)
                ? recordUnitRepository.getByRecord(userRecord)
                : null;

        return getRecordDetailDto(recordDetail, isOptionType, isNumberType, recordUnit);
    }

    private RecordDetailDto getRecordDetailDto(
            RecordDetail recordDetail,
            boolean isOptionType,
            boolean isNumberType,
            RecordUnit recordUnit
    ) {
        List<String> content;

        if (isOptionType) {
            content = recordDetailOptionRepository.findByRecordDetail(recordDetail).stream()
                    .map(option -> option.getRecordOption().getOption() + recordUnit.getUnit())
                    .toList();
        } else if (isNumberType) {
            content = List.of(recordDetail.getContent() + recordUnit.getUnit());
        } else {
            content = List.of(recordDetail.getContent());
        }

        return RecordDetailDto.of(recordDetail, content);
    }

    public List<RecordOption> getRecordOptions(Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isOptionType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_OPTION.getMessage(), METHOD_NOT_RECORD_OPTION.getStatus());
        }
        List<RecordOption> recordOptions = recordOptionRepository.findAllByRecord(record);

        String unit = record.getRecordUnit().getUnit();
        recordOptions.forEach(recordOption -> recordOption.addUnit(unit));
        return recordOptions;
    }

    @Transactional
    public void addRecordOption(RecordOptionAddRequest recordOptionAddRequest, Integer recordId, Integer userId) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isOptionType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_OPTION.getMessage(), METHOD_NOT_RECORD_OPTION.getStatus());
        }

        User user = userRepository.getById(userId);
        record.getRecordOptions()
                .add(RecordOption.builder()
                                .user(user)
                                .userRecord(record)
                                .option(recordOptionAddRequest.option().toUpperCase())
                                .build()
                );
    }

    @Transactional
    public void addRecordUnit(RecordUnitAddRequest recordUnitAddRequest, Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isOptionType(record.getMethod()) && !isNumberType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_NEED_UNIT.getMessage(), METHOD_NOT_NEED_UNIT.getStatus());
        }

        record.updateUnit(recordUnitAddRequest.unit());
    }

    @Transactional
    public void updateRecordOption(
            RecordOptionUpdateRequest recordOptionUpdateRequest,
            Integer recordId,
            Integer optionId
    ) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isOptionType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_OPTION.getMessage(), METHOD_NOT_RECORD_OPTION.getStatus());
        }

        RecordOption recordOption = getRecordOption(optionId);
        recordOption.addOption(recordOptionUpdateRequest.option());
    }

    @Transactional
    public void updateRecord(RecordUpdateRequest recordUpdateRequest, Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        record.updateTitle(recordUpdateRequest.title());
    }
}
