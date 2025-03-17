package com.momenty.record.service;

import static com.momenty.record.exception.RecordExceptionMessage.*;

import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.RecordMethod;
import com.momenty.record.domain.UserRecord;
import com.momenty.record.dto.RecordAddRequest;
import com.momenty.record.repository.RecordRepository;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    public void addRecord(RecordAddRequest recordAddRequest, Integer userId) {
        validMethodType(recordAddRequest.method());
        User user = userRepository.getById(userId);
        UserRecord userRecord = UserRecord.builder()
                .title(recordAddRequest.title())
                .method(RecordMethod.valueOf(recordAddRequest.method()))
                .user(user)
                .build();

        recordRepository.save(userRecord);
        RecordMethod recordMethod = userRecord.getMethod();
        if (isNumberType(recordMethod)) {
            // unit 저장
        }
        if (isOptionType(recordMethod)) {
            // unit 저장, option 저장
        }
    }

    private boolean isNumberType(RecordMethod recordMethod) {
        return RecordMethod.NUMBER_TYPE.equals(recordMethod);
    }

    private boolean isOptionType(RecordMethod recordMethod) {
        return RecordMethod.OPTION_TYPE.equals(recordMethod);
    }

    private void validMethodType(String method) {
        try {
            RecordMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new GlobalException(BAD_RECORD_METHOD.getMessage(), BAD_RECORD_METHOD.getStatus());
        }
    }
}
