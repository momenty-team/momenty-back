package com.momenty.character.service;

import static com.momenty.character.dto.CharacterPromptMessage.CHARACTER_STATUS_OPTIONS;
import static com.momenty.character.dto.CharacterPromptMessage.CHOOSE_STATUS;
import static com.momenty.character.dto.CharacterPromptMessage.USER_DATA_INFO;

import com.momenty.character.dto.CharacterStatusRequest;
import com.momenty.record.dto.RecordAnalysisResponse;
import com.momenty.record.service.RecordService;
import com.momenty.record.util.AiClient;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterService {

    private final RecordService recordService;
    private final AiClient aiClient;

    public String getCharacterStatus(
            CharacterStatusRequest characterStatusRequest, Integer userId, Integer year, Integer month, Integer day
    ) {
        String todayRecordsPrompt = recordService.getTodayRecordsPrompt(userId, year, month, day);

        return requestCharacterStatus(characterStatusRequest.healthKit(), todayRecordsPrompt);
    }

    private String requestCharacterStatus(String healthKit, String todayRecordsPrompt) {
        String prompt =
                USER_DATA_INFO.getMessage()
                + "HealthKit 정보:\n"
                + healthKit + "\n\n"
                + "사용자의 기록 데이터:\n"
                + todayRecordsPrompt + "\n"
                + CHOOSE_STATUS.getMessage()
                + CHARACTER_STATUS_OPTIONS.getMessage();

        return Optional.ofNullable(aiClient.requestSummary(prompt).block())
                .map(RecordAnalysisResponse::result)
                .orElse("응답을 받을 수 없습니다.");
    }
}
