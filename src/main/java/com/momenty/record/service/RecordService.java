package com.momenty.record.service;

import static com.momenty.record.domain.RecordAnalysisMessage.ATYPICAL_OTHER_USERS_RECORD_FEEDBACK_PROMPT;
import static com.momenty.record.domain.RecordAnalysisMessage.CONTENT_AND_PROMPT;
import static com.momenty.record.domain.RecordAnalysisMessage.DATE_AND_CONTENT_SEPARATOR;
import static com.momenty.record.domain.RecordAnalysisMessage.DATE_PATTERN;
import static com.momenty.record.domain.RecordAnalysisMessage.PROMPT;
import static com.momenty.record.domain.RecordAnalysisMessage.RECORDS_FEEDBACK_PROMPT;
import static com.momenty.record.domain.RecordAnalysisMessage.RECORDS_SUMMARY_PROMPT;
import static com.momenty.record.domain.RecordAnalysisMessage.RECORD_CONTENT;
import static com.momenty.record.domain.RecordAnalysisMessage.TREND_PROMPT;
import static com.momenty.record.exception.RecordExceptionMessage.*;

import com.momenty.record.domain.RecordFeedback;
import com.momenty.record.domain.UserRecordAvgTime;
import com.momenty.record.dto.RecordFeedbackRequest;
import com.momenty.record.dto.TextTypeRecordTrend;
import com.momenty.record.repository.RecordFeedbackRepository;
import com.momenty.record.repository.UserRecordAvgTimeRepository;
import java.time.Period;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.momenty.global.exception.GlobalException;
import com.momenty.record.domain.AnalysisPeriod;
import com.momenty.record.domain.RecordAnalysisStatusMessage;
import com.momenty.record.domain.RecordDetail;
import com.momenty.record.domain.RecordDetailOption;
import com.momenty.record.domain.RecordMethod;
import com.momenty.record.domain.RecordOption;
import com.momenty.record.domain.RecordTrendSummary;
import com.momenty.record.domain.RecordUnit;
import com.momenty.record.domain.UserRecord;
import com.momenty.record.dto.NumberTypeRecordTrend;
import com.momenty.record.dto.OXTypeRecordTrend;
import com.momenty.record.dto.OXTypeRecordTrend.OXCount;
import com.momenty.record.dto.OptionTypeRecordTrend;
import com.momenty.record.dto.OptionTypeRecordTrend.OptionDetail;
import com.momenty.record.dto.RecordAddRequest;
import com.momenty.record.dto.RecordAnalysisResponse;
import com.momenty.record.dto.RecordDetailAddRequest;
import com.momenty.record.dto.RecordDetailDto;
import com.momenty.record.dto.RecordDetailUpdateRequest;
import com.momenty.record.dto.RecordOptionAddRequest;
import com.momenty.record.dto.RecordOptionUpdateRequest;
import com.momenty.record.dto.RecordUnitAddRequest;
import com.momenty.record.dto.RecordUpdateRequest;
import com.momenty.record.repository.RecordDetailOptionRepository;
import com.momenty.record.repository.RecordDetailRepository;
import com.momenty.record.repository.RecordOptionRepository;
import com.momenty.record.repository.RecordRepository;
import com.momenty.record.repository.RecordTrendSummaryRepository;
import com.momenty.record.repository.RecordUnitRepository;
import com.momenty.record.util.AiClient;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import com.nimbusds.jose.util.Pair;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final static String NOT_MAKE_SUMMARY = "요약 정보를 생성할 수 없습니다.";

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final RecordOptionRepository recordOptionRepository;
    private final RecordUnitRepository recordUnitRepository;
    private final RecordDetailOptionRepository recordDetailOptionRepository;
    private final RecordDetailRepository recordDetailRepository;
    private final RecordTrendSummaryRepository recordTrendSummaryRepository;
    private final UserRecordAvgTimeRepository avgTimeRepository;
    private final RecordFeedbackRepository recordFeedbackRepository;
    private final AiClient aiClient;

    @Transactional
    public void addRecord(RecordAddRequest recordAddRequest, Integer userId) {
        validMethodType(recordAddRequest.method());
        User user = userRepository.getById(userId);
        UserRecord userRecord = UserRecord.builder()
                .title(recordAddRequest.title())
                .method(RecordMethod.valueOf(recordAddRequest.method().toUpperCase()))
                .isPublic(recordAddRequest.isPublic())
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

    private boolean isOXType(RecordMethod recordMethod) {
        return RecordMethod.BOOLEAN_TYPE.equals(recordMethod);
    }

    private boolean isTextType(RecordMethod recordMethod) {
        return RecordMethod.TEXT_TYPE.equals(recordMethod);
    }

    private void validMethodType(String method) {
        try {
            RecordMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(BAD_RECORD_METHOD.getMessage(), BAD_RECORD_METHOD.getStatus());
        }
    }

    public List<UserRecord> getRecords(Integer userId, Integer year, Integer month, Integer day) {
        Pair<LocalDateTime, LocalDateTime> dateFilter = makeDateFilter(year, month, day);
        LocalDateTime startDate = dateFilter.getLeft();
        LocalDateTime endDate = dateFilter.getRight();

        User user = userRepository.getById(userId);

        List<UserRecord> records;
        if (startDate != null && endDate != null) {
            records = recordRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(user, startDate, endDate);
        } else {
            records = recordRepository.findAllByUserOrderByCreatedAtDesc(user);
        }
        return records;
    }

    @Transactional
    public void addRecordDetail(
            Integer recordId, RecordDetailAddRequest recordDetailAddRequest, Integer year, Integer month, Integer day
    ) {
        LocalDateTime createdAt = LocalDateTime.of(
                year, month, day,
                LocalTime.now().getHour(),
                LocalTime.now().getMinute(),
                LocalTime.now().getSecond()
        );

        UserRecord record = recordRepository.getById(recordId);
        RecordDetail recordDetail = RecordDetail.builder()
                .content(recordDetailAddRequest.content())
                .record(record)
                .isPublic(recordDetailAddRequest.isPublic())
                .createdAt(createdAt)
                .build();
        recordDetailRepository.save(recordDetail);

        if (isOptionType(record.getMethod()) && recordDetailAddRequest.optionIds() != null) {
            List<RecordDetailOption> options = recordDetailAddRequest.optionIds().stream()
                    .map(optionId -> {
                        RecordOption recordOption = getRecordOption(optionId);
                        return RecordDetailOption.builder()
                                .recordDetail(recordDetail)
                                .recordOption(recordOption)
                                .build();
                    })
                    .toList();

            recordDetailOptionRepository.saveAll(options);
        }

        Integer createdHour = recordDetail.getCreatedAt().getHour();
        updateAverageHour(record, createdHour);
        removeRecordSummary(record);
    }

    private void removeRecordSummary(UserRecord record) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        recordTrendSummaryRepository.deleteByRecordAndCreatedAtBetween(record, startOfDay, endOfDay);
    }

    public void updateAverageHour(UserRecord record, Integer newHour) {
        User user = record.getUser();

        Optional<UserRecordAvgTime> optionalAvgTime = avgTimeRepository.findByUserAndRecord(user, record);

        UserRecordAvgTime avgTime;
        if (optionalAvgTime.isPresent()) {
            avgTime = optionalAvgTime.get();
            avgTime.updateAverageHour(newHour);
        } else {
            avgTime = new UserRecordAvgTime(user, record, newHour);
        }
        avgTimeRepository.save(avgTime);
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
            recordDetails = recordDetailRepository.findByRecordAndCreatedAtBetweenOrderByCreatedAtDesc(userRecord, startDate, endDate);
        } else {
            recordDetails = recordDetailRepository.findAllByRecordOrderByCreatedAtDesc(userRecord);
        }

        return getRecordDetailDtos(userRecord, recordDetails);
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

        if (isOptionType) {
            return getOptionTypeRecordDetailDtoWithId(recordDetail);
        }

        return getRecordDetailDto(recordDetail, false, isNumberType, recordUnit);
    }

    private RecordDetailDto getOptionTypeRecordDetailDtoWithId(RecordDetail recordDetail) {
        List<String> content = recordDetailOptionRepository.findByRecordDetail(recordDetail).stream()
                .map(option -> option.getRecordOption().getId().toString())
                .toList();

        return RecordDetailDto.of(recordDetail, content);
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
            content = List.of(recordDetail.getContent() + " " + recordUnit.getUnit());
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
        List<RecordOption> recordOptions = recordOptionAddRequest.options().stream()
                .map(option -> RecordOption.builder()
                        .user(user)
                        .userRecord(record)
                        .option(option.toUpperCase())
                        .build())
                .toList();

        record.getRecordOptions().addAll(recordOptions);
    }

    @Transactional
    public void addRecordUnit(RecordUnitAddRequest recordUnitAddRequest, Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isOptionType(record.getMethod()) && !isNumberType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_NEED_UNIT.getMessage(), METHOD_NOT_NEED_UNIT.getStatus());
        }

        record.updateUnit(recordUnitAddRequest.unit());
        removeRecordSummary(record);
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

        removeRecordSummary(record);
    }

    @Transactional
    public void updateRecord(RecordUpdateRequest recordUpdateRequest, Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        record.update(recordUpdateRequest.title(), recordUpdateRequest.isPublic());

        removeRecordSummary(record);
    }

    @Transactional
    public void updateRecordDetail(
            RecordDetailUpdateRequest recordDetailUpdateRequest,
            Integer detailId
    ) {
        RecordDetail recordDetail = recordDetailRepository.getById(detailId);
        UserRecord record = recordDetail.getRecord();

        recordDetail.update(
                recordDetailUpdateRequest.content(),
                recordDetailUpdateRequest.isPublic(),
                recordDetailUpdateRequest.hour(),
                recordDetailUpdateRequest.minute()
        );

        if (isOptionType(record.getMethod())) {
            recordDetailOptionRepository.deleteAllByRecordDetail(recordDetail);

            List<Integer> optionIds = parseOptionIds(recordDetailUpdateRequest.content());

            List<RecordOption> recordOptions = optionIds.stream()
                    .map(this::getRecordOption)
                    .toList();

            List<RecordDetailOption> newOptions = recordOptions.stream()
                    .map(option -> RecordDetailOption.builder()
                            .recordDetail(recordDetail)
                            .recordOption(option)
                            .build())
                    .toList();

            recordDetailOptionRepository.saveAll(newOptions);
        }

        removeRecordSummary(record);
    }

    private List<Integer> parseOptionIds(String content) {
        try {
            return Arrays.stream(content.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .toList();
        } catch (NumberFormatException e) {
            throw new GlobalException(BAD_OPTION_ID.getMessage(), BAD_OPTION_ID.getStatus());
        }
    }


    @Transactional
    public void deleteRecord(Integer recordId) {
        recordRepository.getById(recordId);
        recordRepository.deleteById(recordId);
    }

    @Transactional
    public void deleteRecordDetail(Integer detailId) {
        recordDetailRepository.getById(detailId);
        recordDetailRepository.deleteById(detailId);
    }

    @Transactional
    public void deleteRecordOption(Integer optionId) {
        RecordOption recordOption = getRecordOption(optionId);
        List<RecordDetailOption> recordDetailOptions = recordDetailOptionRepository.findAllByRecordOption(recordOption);

        if (!recordDetailOptions.isEmpty()) {
            throw new GlobalException(USED_OPTION_NOT_DELETE.getMessage(), USED_OPTION_NOT_DELETE.getStatus());
        }
        recordOptionRepository.deleteById(optionId);
    }

    public Mono<RecordAnalysisResponse> analyzeRecord(Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        List<RecordDetail> recordDetails = recordDetailRepository.findAllByRecordOrderByCreatedAtDesc(record);

        boolean isOptionType = isOptionType(record.getMethod());
        boolean isNumberType = isNumberType(record.getMethod());

        RecordUnit recordUnit = (isOptionType || isNumberType)
                ? recordUnitRepository.getByRecord(record)
                : null;

        List<RecordDetailDto> recordDetailDtos =  recordDetails.stream()
                .map(recordDetail -> getRecordDetailDto(recordDetail, isOptionType, isNumberType, recordUnit))
                .toList();

        String prompt = buildPrompt(recordDetailDtos, record.getTitle());
        return aiClient.requestSummary(prompt);
    }

    private String buildPrompt(List<RecordDetailDto> recordDetails, String recordTitle) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN.getMessage());

        String promptChoices = Arrays.stream(RecordAnalysisStatusMessage.values())
                .map(RecordAnalysisStatusMessage::getMessage)
                .collect(Collectors.joining("\n"));

        String recordContent = recordDetails.stream()
                .sorted(Comparator.comparing(RecordDetailDto::createdAt).reversed())
                .limit(20)
                .map(detail -> detail.createdAt().format(formatter)
                        + DATE_AND_CONTENT_SEPARATOR.getMessage()
                        + String.join(", ", detail.content().toString())
                )
                .collect(Collectors.joining(CONTENT_AND_PROMPT.getMessage()));

        return PROMPT.getMessage()
                + "\n\n" + promptChoices
                + "\n\n" + recordTitle + ":\n"
                + recordContent;
    }

    private String buildPrompt(Map<String, List<RecordDetailDto>> titleToDtosMap) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN.getMessage());

        String promptChoices = Arrays.stream(RecordAnalysisStatusMessage.values())
                .map(RecordAnalysisStatusMessage::getMessage)
                .collect(Collectors.joining("\n"));

        StringBuilder fullContent = new StringBuilder();

        for (Map.Entry<String, List<RecordDetailDto>> entry : titleToDtosMap.entrySet()) {
            String title = entry.getKey(); // 기록 제목
            List<RecordDetailDto> dtos = entry.getValue();

            String recordContent = dtos.stream()
                    .sorted(Comparator.comparing(RecordDetailDto::createdAt).reversed())
                    .limit(10)
                    .map(detail -> detail.createdAt().format(formatter)
                            + DATE_AND_CONTENT_SEPARATOR.getMessage()
                            + String.join(", ", detail.content()))
                    .collect(Collectors.joining(CONTENT_AND_PROMPT.getMessage()));

            // 기록 제목 포함
            fullContent.append("[").append(title).append("]").append("\n")
                    .append(recordContent)
                    .append("\n\n");
        }

        return PROMPT.getMessage()
                + "\n\n" + promptChoices
                + "\n\n" + RECORD_CONTENT.getMessage() + "\n"
                + fullContent;
    }

    private List<RecordDetailDto> getRecordDetailDtos(UserRecord record, List<RecordDetail> details) {
        boolean isOptionType = isOptionType(record.getMethod());
        boolean isNumberType = isNumberType(record.getMethod());
        RecordUnit recordUnit = (isOptionType || isNumberType)
                ? recordUnitRepository.getByRecord(record)
                : null;

        return details.stream()
                .map(detail -> getRecordDetailDto(detail, isOptionType, isNumberType, recordUnit))
                .toList();
    }

    private void validPeriod(String period) {
        try {
            AnalysisPeriod.valueOf(period.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(BAD_PERIOD.getMessage(), BAD_PERIOD.getStatus());
        }
    }

    private Pair<LocalDateTime, LocalDateTime> getPeriodRange(String period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (AnalysisPeriod.valueOf(period.toUpperCase())) {
            case TODAY -> Pair.of(now.toLocalDate().atStartOfDay(), now.toLocalDate().atTime(23, 59, 59));
            case WEEK -> Pair.of(now.minusDays(6).toLocalDate().atStartOfDay(), now.toLocalDate().atTime(23, 59, 59));
            case MONTH -> Pair.of(now.withDayOfMonth(1).toLocalDate().atStartOfDay(), now.toLocalDate().atTime(23, 59, 59));
        };
    }

    public UserRecord getRecord(Integer recordId) {
        return recordRepository.getById(recordId);
    }

    public RecordUnit getRecordUnit(Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        RecordMethod recordMethod = record.getMethod();
        if (!isNumberType(recordMethod) && !isOptionType(recordMethod)) {
            throw new GlobalException(METHOD_NOT_NEED_UNIT.getMessage(), METHOD_NOT_NEED_UNIT.getStatus());
        }
        return recordUnitRepository.getByRecord(record);
    }

    public NumberTypeRecordTrend getNumberTypeRecordTrend(Integer recordId, Integer year, Integer month, Integer day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        LocalDateTime endDate = targetDate.atTime(LocalTime.MAX);
        LocalDateTime startDate = targetDate.minusDays(6).atStartOfDay();

        UserRecord record = recordRepository.getById(recordId);
        if (!isNumberType(record.getMethod())) {
            throw  new GlobalException(METHOD_NOT_RECORD_NUMBER.getMessage(), METHOD_NOT_RECORD_NUMBER.getStatus());
        }

        String unit = recordUnitRepository.getByRecord(record).getUnit();
        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record, startDate, endDate);

        Map<LocalDate, Long> countsByDate = thisWeekRecord.stream()
                .collect(Collectors.groupingBy(
                        recordDetail -> recordDetail.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        List<NumberTypeRecordTrend.Data> data = startDate.toLocalDate().datesUntil(endDate.toLocalDate().plusDays(1))
                .map(date -> {
                    String weekDay = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
                    Long count = countsByDate.getOrDefault(date, 0L);
                    String valueWithUnit = count + unit;
                    return new NumberTypeRecordTrend.Data(date, weekDay, valueWithUnit);
                })
                .toList();

        int totalCount = thisWeekRecord.size();
        double averageCount = totalCount / 7.0;
        int roundedAverage = (int) Math.round(averageCount);

        return NumberTypeRecordTrend.of(
                startDate.toLocalDate(),
                endDate.toLocalDate(),
                data,
                totalCount,
                roundedAverage
        );
    }

    private String generateTrendSummary(UserRecord record, List<RecordDetail> thisWeekRecord) {
        String prompt = buildPrompt(record, thisWeekRecord);
        RecordAnalysisResponse response = aiClient.requestSummary(prompt).block();
        return Optional.ofNullable(response)
                .map(RecordAnalysisResponse::result)
                .orElse(NOT_MAKE_SUMMARY);
    }

    private List<RecordDetail> findThisWeekRecord(UserRecord record, LocalDateTime startDate, LocalDateTime endDate) {
        return recordDetailRepository.findByRecordAndCreatedAtBetweenOrderByCreatedAtDesc(record, startDate, endDate);
    }

    private Map<DayOfWeek, Long> getCountsByDayOfWeek(List<RecordDetail> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getCreatedAt().getDayOfWeek(),
                        () -> new EnumMap<>(DayOfWeek.class),
                        Collectors.counting()
                ));
    }

    private String buildPrompt(UserRecord record, List<RecordDetail> details) {
        List<RecordDetailDto> recordDetailDtos = getRecordDetailDtos(record, details);
        StringBuilder prompt = writeRecordDetailPrompt(record, recordDetailDtos, 70);

        return TREND_PROMPT.getMessage()
                + RECORD_CONTENT.getMessage() + "\n"
                + prompt;
    }

    private StringBuilder writeRecordDetailPrompt(UserRecord record, List<RecordDetailDto> recordDetailDtos, Integer maxSize) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN.getMessage());

        StringBuilder fullContent = new StringBuilder();

        String title = record.getTitle();
        String recordContent = recordDetailDtos.stream()
                .sorted(Comparator.comparing(RecordDetailDto::createdAt).reversed())
                .limit(maxSize)
                .map(detail -> detail.createdAt().format(formatter)
                        + DATE_AND_CONTENT_SEPARATOR.getMessage()
                        + String.join(", ", detail.content()))
                .collect(Collectors.joining(CONTENT_AND_PROMPT.getMessage()));

        fullContent.append("[").append(title).append("]").append("\n")
                .append(recordContent)
                .append("\n\n");

        return fullContent;
    }

    @Transactional
    public RecordTrendSummary getTrendSummary(Integer recordId, Integer year, Integer month, Integer day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        LocalDateTime endDate = targetDate.atTime(LocalTime.MAX);
        LocalDateTime startDate = targetDate.minusDays(6).atStartOfDay();

        UserRecord record = recordRepository.getById(recordId);
        Optional<RecordTrendSummary> trendSummary =
                findTodayTrendSummary(record, targetDate.atStartOfDay(), targetDate.atTime(LocalTime.MAX));
        if (trendSummary.isPresent()) {
            return trendSummary.get();
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record, startDate, endDate);
        String summary = generateTrendSummary(record, thisWeekRecord);

        RecordTrendSummary recordTrendSummary = RecordTrendSummary.builder()
                .record(record)
                .content(summary)
                .user(record.getUser())
                .build();
        return recordTrendSummaryRepository.save(recordTrendSummary);
    }

    private Optional<RecordTrendSummary> findTodayTrendSummary(
            UserRecord record, LocalDateTime startDate, LocalDateTime endDate
    ) {
        return recordTrendSummaryRepository.findFirstByRecordAndCreatedAtBetweenOrderByCreatedAtDesc(record, startDate, endDate);
    }

    public OXTypeRecordTrend getOXTypeRecordTrend(Integer recordId, Integer year, Integer month, Integer day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        LocalDateTime endDate = targetDate.atTime(LocalTime.MAX);
        LocalDateTime startDate = targetDate.minusDays(6).atStartOfDay();

        UserRecord record = recordRepository.getById(recordId);
        if (!isOXType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_OX.getMessage(), METHOD_NOT_RECORD_OX.getStatus());
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record, startDate, endDate);

        Map<LocalDate, OXTypeRecordTrend.OXCount> countsByDate = thisWeekRecord.stream()
                .collect(Collectors.groupingBy(
                        recordDetail -> recordDetail.getCreatedAt().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                details -> {
                                    int oCount = (int) details.stream().filter(d -> "O".equals(d.getContent())).count();
                                    int xCount = (int) details.stream().filter(d -> "X".equals(d.getContent())).count();
                                    return new OXTypeRecordTrend.OXCount(oCount, xCount);
                                }
                        )
                ));

        List<OXTypeRecordTrend.Data> data = startDate.toLocalDate().datesUntil(endDate.toLocalDate().plusDays(1))
                .map(date -> {
                    String weekDay = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
                    OXTypeRecordTrend.OXCount oxCount = countsByDate.getOrDefault(date, new OXTypeRecordTrend.OXCount(0, 0));
                    return new OXTypeRecordTrend.Data(date, weekDay, oxCount);
                })
                .toList();

        int totalOCount = data.stream().mapToInt(d -> d.oxCount().oCount()).sum();
        int totalXCount = data.stream().mapToInt(d -> d.oxCount().xCount()).sum();
        OXTypeRecordTrend.OXCount totalCounts = new OXTypeRecordTrend.OXCount(totalOCount, totalXCount);

        int totalCount = totalOCount + totalXCount;
        double averageCount = totalCount / 7.0;
        int roundedAverage = (int) Math.round(averageCount);

        return OXTypeRecordTrend.of(startDate.toLocalDate(), endDate.toLocalDate(), data, totalCounts, roundedAverage);
    }

    private Map<DayOfWeek, OXCount> getOXCountsByDayOfWeek(List<RecordDetail> records) {
        Map<DayOfWeek, OXCount> countsByDay = new EnumMap<>(DayOfWeek.class);

        for (RecordDetail record : records) {
            DayOfWeek dayOfWeek = record.getCreatedAt().getDayOfWeek();
            boolean isO = "O".equals(record.getContent());
            boolean isX = "X".equals(record.getContent());

            OXCount currentCount = countsByDay.getOrDefault(dayOfWeek, new OXCount(0, 0));
            int newOCount = currentCount.oCount() + (isO ? 1 : 0);
            int newXCount = currentCount.xCount() + (isX ? 1 : 0);

            countsByDay.put(dayOfWeek, new OXCount(newOCount, newXCount));
        }

        return countsByDay;
    }

    private OXCount calculateTotalCounts(Map<DayOfWeek, OXCount> countsByDay) {
        int totalO = 0;
        int totalX = 0;

        for (OXCount count : countsByDay.values()) {
            totalO += count.oCount();
            totalX += count.xCount();
        }

        return new OXCount(totalO, totalX);
    }

    public OptionTypeRecordTrend getOptionTypeRecordTrend(Integer recordId, Integer year, Integer month, Integer day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        LocalDateTime endDate = targetDate.atTime(LocalTime.MAX);
        LocalDateTime startDate = targetDate.minusDays(6).atStartOfDay();

        UserRecord record = recordRepository.getById(recordId);
        if (!isOptionType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_OPTION.getMessage(), METHOD_NOT_RECORD_OPTION.getStatus());
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record, startDate, endDate);

        String unit = recordUnitRepository.getByRecord(record).getUnit();
        Map<OptionDetail, Long> optionFrequencyMap = new HashMap<>();

        List<OptionTypeRecordTrend.Data> data = startDate.toLocalDate().datesUntil(endDate.toLocalDate().plusDays(1))
                .map(date -> {
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    String week = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);

                    List<RecordDetail> recordsForDay = thisWeekRecord.stream()
                            .filter(recordDetail -> recordDetail.getCreatedAt().toLocalDate().equals(date))
                            .collect(Collectors.toList());

                    return createDataRecord(date, week, recordsForDay, optionFrequencyMap, unit);
                })
                .collect(Collectors.toList());

        int totalCount = thisWeekRecord.size();

        OptionTypeRecordTrend.OptionDetail mostFrequentOption = getMostFrequentOption(optionFrequencyMap);

        return OptionTypeRecordTrend.of(startDate.toLocalDate(), endDate.toLocalDate(), data, totalCount, mostFrequentOption);

    }

    private OptionTypeRecordTrend.Data createDataRecord(
            LocalDate date,
            String week,
            List<RecordDetail> records,
            Map<OptionTypeRecordTrend.OptionDetail, Long> optionFrequencyMap,
            String unit
    ) {
        if (records == null || records.isEmpty()) {
            return new OptionTypeRecordTrend.Data(date, week, Collections.emptyList(), 0);
        }

        List<OptionTypeRecordTrend.OptionDetail> optionDetails = records.stream()
                .flatMap(record -> record.getRecordDetailOptions().stream())
                .map(option -> {
                    RecordOption recordOption = option.getRecordOption();
                    String optionValueWithUnit = recordOption.getOption() + unit;
                    OptionTypeRecordTrend.OptionDetail optionDetail =
                            new OptionTypeRecordTrend.OptionDetail(recordOption.getId(), optionValueWithUnit);

                    optionFrequencyMap.put(optionDetail, optionFrequencyMap.getOrDefault(optionDetail, 0L) + 1);
                    return optionDetail;
                })
                .collect(Collectors.toList());

        return new OptionTypeRecordTrend.Data(date, week, optionDetails, optionDetails.size());
    }

    private OptionDetail getMostFrequentOption(Map<OptionDetail, Long> optionFrequencyMap) {
        return optionFrequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(new OptionTypeRecordTrend.OptionDetail(0, "NONE"));
    }

    public TextTypeRecordTrend getTextTypeRecordTrend(Integer recordId, Integer year, Integer month, Integer day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        LocalDateTime endDate = targetDate.atTime(LocalTime.MAX);
        LocalDateTime startDate = targetDate.minusDays(6).atStartOfDay();

        UserRecord record = recordRepository.getById(recordId);
        if (!isTextType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_TEXT.getMessage(), METHOD_NOT_RECORD_TEXT.getStatus());
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record, startDate, endDate);

        Map<LocalDate, Long> recordCountByDate = thisWeekRecord.stream()
                .collect(Collectors.groupingBy(
                        recordDetail -> recordDetail.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        List<TextTypeRecordTrend.Data> data = startDate.toLocalDate().datesUntil(endDate.toLocalDate().plusDays(1))
                .map(date -> {
                    String week = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
                    int count = recordCountByDate.getOrDefault(date, 0L).intValue();
                    return new TextTypeRecordTrend.Data(date, week, count);
                })
                .toList();

        int totalCount = thisWeekRecord.size();
        double averageCount = totalCount / 7.0;
        int roundedAverage = (int) Math.round(averageCount);

        return TextTypeRecordTrend.of(startDate.toLocalDate(), endDate.toLocalDate(), data, totalCount, roundedAverage);
    }

    public String getRecordsSummary(Integer userId, Integer year, Integer month, Integer day) {
        Pair<LocalDateTime, LocalDateTime> dateFilter = makeDateFilter(year, month, day);
        LocalDateTime startDate = dateFilter.getLeft();
        LocalDateTime endDate = dateFilter.getRight();

        User user = userRepository.getById(userId);

        List<UserRecord> userRecords = recordRepository.findAllByUser(user);
        if (userRecords.isEmpty()) {
            return "";
        }

        List<RecordDetail> recordDetails = recordDetailRepository
                        .findByRecordInAndCreatedAtBetweenOrderByCreatedAtDesc(userRecords, startDate, endDate)
                        .stream()
                        .limit(200)
                        .toList();
        if (recordDetails.isEmpty()) {
            return "";
        }

        Map<UserRecord, List<RecordDetailDto>> recordDetailDtoMap = userRecords.stream()
                .collect(Collectors.toMap(
                        record -> record,
                        record -> {
                            List<RecordDetail> detailsForRecord = recordDetails.stream()
                                    .filter(detail -> detail.getRecord().equals(record))
                                    .toList();
                            return toRecordDetailDtos(record, detailsForRecord);
                        }
                ));

        return requestGptForRecordSummary(recordDetailDtoMap);
    }

    private String requestGptForRecordSummary(Map<UserRecord, List<RecordDetailDto>> recordDetailDtoMap) {
        StringBuilder promptBuilder = new StringBuilder(RECORDS_SUMMARY_PROMPT.getMessage());

        for (Map.Entry<UserRecord, List<RecordDetailDto>> entry : recordDetailDtoMap.entrySet()) {
            UserRecord record = entry.getKey();
            String topic = record.getTitle();
            List<RecordDetailDto> details = entry.getValue();

            promptBuilder.append("주제: ").append(topic).append("\n");
            for (RecordDetailDto detail : details) {
                promptBuilder.append("- ").append(detail.content()).append("\n");
            }
            promptBuilder.append("\n");
        }

        String prompt = promptBuilder.toString();

        return Optional.ofNullable(aiClient.requestSummary(prompt).block())
                .map(RecordAnalysisResponse::result)
                .orElse("");
    }

    private List<RecordDetailDto> toRecordDetailDtos(UserRecord record, List<RecordDetail> details) {
        boolean isOptionType = isOptionType(record.getMethod());
        boolean isNumberType = isNumberType(record.getMethod());

        RecordUnit recordUnit = (isOptionType || isNumberType)
                ? recordUnitRepository.getByRecord(record)
                : null;

        return details.stream()
                .map(detail -> getRecordDetailDto(detail, isOptionType, isNumberType, recordUnit))
                .toList();
    }

    @Transactional
    public RecordFeedback getRecordFeedback(
            RecordFeedbackRequest recordFeedbackRequest,
            Integer year, Integer month, Integer day, Integer userId
    ) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        LocalDateTime startDate = targetDate.minusDays(7).atStartOfDay();
        LocalDateTime endDate = targetDate.atTime(LocalTime.MAX);

        User user = userRepository.getById(userId);

        Optional<RecordFeedback> recordFeedback =
                recordFeedbackRepository.findFirstByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
                        user, targetDate.atStartOfDay(), targetDate.atTime(LocalTime.MAX)
                );

        if (recordFeedback.isPresent()) {
            return recordFeedback.get();
        }

        Pageable pageable = PageRequest.of(0, 1000);
        List<RecordDetail> recordDetails = recordDetailRepository.findRecentDetails(userId, startDate, endDate, pageable);

        Map<UserRecord, List<RecordDetail>> recordDetailMap = recordDetails.stream()
                .collect(Collectors.groupingBy(RecordDetail::getRecord));

        String separateByTopicRecord = buildPromptRecords(separateByTopic(recordDetailMap));

        pageable = PageRequest.of(0, 500);
        List<RecordFeedback> otherUsersRecordFeedbacks = getSimilarUserFeedbacks(user, pageable);
        String atypicalOtherUsersRecordFeedback = requestAtypicalOtherUsersRecordFeedback(otherUsersRecordFeedbacks);

        String gender = (user.getGender() != null) ? user.getGender().name() : "정보 없음";
        int age = (user.getBirthDate() != null) ? Period.between(user.getBirthDate(), LocalDate.now()).getYears() : 0;

        String userInfo = "성별: " + gender + ", 나이: " + age + ", 닉네임: " + user.getNickname();


        String result =
                requestGptForRecordFeedback(
                        userInfo, separateByTopicRecord, recordFeedbackRequest.healthKit(), atypicalOtherUsersRecordFeedback
                );

        return recordFeedbackRepository.save(createRecordFeedback(user, result, targetDate));
    }

    private String buildPromptRecords(
            Map<UserRecord, List<RecordDetailDto>> separateByTopicRecord
    ) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<UserRecord, List<RecordDetailDto>> entry : separateByTopicRecord.entrySet()) {
            UserRecord userRecord = entry.getKey();
            List<RecordDetailDto> detailList = entry.getValue();

            result.append("주제: ").append(userRecord.getTitle()).append("\n");

            for (RecordDetailDto dto : detailList) {
                result.append("- ")
                        .append(dto.createdAt())
                        .append(": ")
                        .append(String.join(", ", dto.content()))
                        .append("\n");
            }

            result.append("\n");
        }
        return result.toString();
    }

    private String requestAtypicalOtherUsersRecordFeedback(List<RecordFeedback> otherUsersRecordFeedbacks) {
        StringBuilder promptBuilder = new StringBuilder(ATYPICAL_OTHER_USERS_RECORD_FEEDBACK_PROMPT.getMessage());

        promptBuilder.append("다른 사용자들의 일주일간 기록과 헬스키트 피드백 데이터:\n");
        for (RecordFeedback feedback : otherUsersRecordFeedbacks) {
            String content = feedback.getContent();
            promptBuilder.append("- ").append(content).append("\n");
        }

        promptBuilder.append("\n");
        String prompt = promptBuilder.toString();

        return Optional.ofNullable(aiClient.requestSummary(prompt).block())
                .map(RecordAnalysisResponse::result)
                .orElse("");
    }

    private List<RecordFeedback> getSimilarUserFeedbacks(User currentUser, Pageable pageable) {
        int userAge = Period.between(currentUser.getBirthDate(), LocalDate.now()).getYears();
        int minAge = userAge - 5;
        int maxAge = userAge + 5;

        LocalDate minBirthDate = LocalDate.now().minusYears(maxAge);
        LocalDate maxBirthDate = LocalDate.now().minusYears(minAge);

        return recordFeedbackRepository.findRecentFeedbackFromSimilarUsers(
                currentUser.getId(),
                currentUser.getGender(),
                minBirthDate,
                maxBirthDate,
                pageable
        );
    }

    private String requestGptForRecordFeedback(
            String userInfo,
            String recordSummary, String healthKit,
            String otherUsersRecordSummary
    ) {
        String prompt = userInfo + "\n\n"
                + "사용자 기록 데이터:\n"
                + recordSummary
                + "\n\n"
                + "헬스키트 정보:\n"
                + healthKit
                + "\n\n"
                + "다른 사용자들의 기록 요약:\n"
                + otherUsersRecordSummary
                + "\n\n"
                + RECORDS_FEEDBACK_PROMPT.getMessage();

        log.info("요청한 프롬프트: {}", prompt);

        return Optional.ofNullable(aiClient.requestSummary(prompt).block())
                .map(RecordAnalysisResponse::result)
                .orElse("");
    }

    private Map<UserRecord, List<RecordDetailDto>> separateByTopic(Map<UserRecord, List<RecordDetail>> recordDetailMap) {
        return recordDetailMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> {
                            UserRecord record = entry.getKey();
                            List<RecordDetail> details = entry.getValue();

                            return getRecordDetailDtos(record, details);
                        }
                ));
    }

    private RecordFeedback createRecordFeedback(User user, String result, LocalDate date) {
        if (result == null || result.isBlank()) {
            throw new GlobalException(AI_MAKE_BAD_RESPONSE.getMessage(), AI_MAKE_BAD_RESPONSE.getStatus());
        }

        Map<String, String> fieldMap = new HashMap<>();

        Arrays.stream(result.split("\n"))
                .filter(line -> line.contains(": "))
                .forEach(line -> {
                    String[] parts = line.split(": ", 2);
                    if (parts.length == 2) {
                        fieldMap.put(parts[0].trim(), parts[1].trim());
                    }
                });

        String title = fieldMap.get("title");
        String level = fieldMap.get("level");
        String feedback = fieldMap.get("feedback");

        if (title == null || level == null || feedback == null) {
            throw new GlobalException(AI_MAKE_BAD_RESPONSE.getMessage(), AI_MAKE_BAD_RESPONSE.getStatus());
        }

        return RecordFeedback.builder()
                .title(title)
                .level(level)
                .content(feedback)
                .user(user)
                .createdAt(date.atTime(LocalTime.now()))
                .build();
    }

    public List<RecordFeedback> getRecordFeedbacks(Integer userId) {
        User user = userRepository.getById(userId);
        return recordFeedbackRepository.findAllByUser(user);
    }
}
