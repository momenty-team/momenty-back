package com.momenty.record.service;

import static com.momenty.record.exception.RecordExceptionMessage.*;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.RecordMethod;
import com.momenty.record.domain.RecordOption;
import com.momenty.record.domain.RecordUnit;
import com.momenty.record.domain.UserRecord;
import com.momenty.record.dto.RecordAddRequest;
import com.momenty.record.repository.RecordOptionRepository;
import com.momenty.record.repository.RecordRepository;
import com.momenty.record.repository.RecordUnitRepository;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
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
}
