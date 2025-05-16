package com.momenty.record.service;

import static com.momenty.record.domain.RecordAnalysisMessage.CONTENT_AND_PROMPT;
import static com.momenty.record.domain.RecordAnalysisMessage.DATE_AND_CONTENT_SEPARATOR;
import static com.momenty.record.domain.RecordAnalysisMessage.DATE_PATTERN;
import static com.momenty.record.domain.RecordAnalysisMessage.PROMPT;
import static com.momenty.record.domain.RecordAnalysisMessage.RECORDS_SUMMARY_PROMPT;
import static com.momenty.record.domain.RecordAnalysisMessage.RECORD_CONTENT;
import static com.momenty.record.domain.RecordAnalysisMessage.TREND_PROMPT;
import static com.momenty.record.exception.RecordExceptionMessage.*;

import com.momenty.record.dto.TextTypeRecordTrend;
import java.util.Collections;
import java.util.HashMap;
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
import com.momenty.record.dto.OptionTypeRecordTrend.DayRecord;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
    public void addRecordDetail(Integer recordId, RecordDetailAddRequest recordDetailAddRequest) {
        UserRecord record = recordRepository.getById(recordId);
        RecordDetail recordDetail = RecordDetail.builder()
                .content(recordDetailAddRequest.content())
                .record(record)
                .isPublic(recordDetailAddRequest.isPublic())
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
        record.update(recordUpdateRequest.title(), recordUpdateRequest.isPublic());
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


    public Mono<RecordAnalysisResponse> analyzeRecords(String period, Integer userId) {
        validPeriod(period);
        User user = userRepository.getById(userId);;
        Pair<LocalDateTime, LocalDateTime> range = getPeriodRange(period);

        Map<UserRecord, List<RecordDetail>> recordDetailsMap = user.getRecords().stream()
                .collect(Collectors.toMap(
                        record -> record,
                        record -> recordDetailRepository.findByRecordAndCreatedAtBetweenOrderByCreatedAtDesc(record, range.getLeft(), range.getRight())
                ));

        Map<String, List<RecordDetailDto>> titleToDtosMap = recordDetailsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getTitle(),
                        entry -> {
                            UserRecord record = entry.getKey();
                            List<RecordDetail> details = entry.getValue();

                            return getRecordDetailDtos(record, details);
                        }
                ));

        String prompt = buildPrompt(titleToDtosMap);
        System.out.println(prompt);
        return aiClient.requestSummary(prompt);
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

    public NumberTypeRecordTrend getNumberTypeRecordTrend(Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isNumberType(record.getMethod())) {
            throw  new GlobalException(METHOD_NOT_RECORD_NUMBER.getMessage(), METHOD_NOT_RECORD_NUMBER.getStatus());
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record);
        Map<DayOfWeek, Long> countsByDay = getCountsByDayOfWeek(thisWeekRecord);
        int totalCount = thisWeekRecord.size();
        double averageCount = totalCount / 7.0;
        int roundedAverage = (int) Math.round(averageCount);

        return NumberTypeRecordTrend.of(
                countsByDay.getOrDefault(DayOfWeek.MONDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.TUESDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.WEDNESDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.THURSDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.FRIDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.SATURDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.SUNDAY, 0L).intValue(),
                totalCount,
                roundedAverage
        );
    }

    private String generateTrendSummary(UserRecord record, List<RecordDetail> thisWeekRecord) {
        String prompt = buildPrompt(record, thisWeekRecord);
        System.out.println(prompt);
        RecordAnalysisResponse response = aiClient.requestSummary(prompt).block();
        return Optional.ofNullable(response)
                .map(RecordAnalysisResponse::result)
                .orElse(NOT_MAKE_SUMMARY);
    }

    private List<RecordDetail> findThisWeekRecord(UserRecord record) {
        return recordDetailRepository.findByRecordAndCreatedAtBetweenOrderByCreatedAtDesc(
                record,
                LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        );
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
                + "\n\n" + RECORD_CONTENT.getMessage() + "\n"
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
    public RecordTrendSummary getTrendSummary(Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        Optional<RecordTrendSummary> trendSummary = findTodayTrendSummary(record);
        if (trendSummary.isPresent()) {
            return trendSummary.get();
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record);
        String summary = generateTrendSummary(record, thisWeekRecord);

        RecordTrendSummary recordTrendSummary = RecordTrendSummary.builder()
                .record(record)
                .content(summary)
                .build();
        return recordTrendSummaryRepository.save(recordTrendSummary);
    }

    private Optional<RecordTrendSummary> findTodayTrendSummary(UserRecord record) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return recordTrendSummaryRepository.findByRecordAndCreatedAtBetween(record, startOfDay, endOfDay);
    }

    public OXTypeRecordTrend getOXTypeRecordTrend(Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isOXType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_OX.getMessage(), METHOD_NOT_RECORD_OX.getStatus());
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record);

        Map<DayOfWeek, OXCount> countsByDay = getOXCountsByDayOfWeek(thisWeekRecord);
        OXCount totalCounts = calculateTotalCounts(countsByDay);
        double averageCount = (totalCounts.oCount() + totalCounts.xCount()) / 7.0;
        int roundedAverage = (int) Math.round(averageCount);

        return OXTypeRecordTrend.of(
                countsByDay.getOrDefault(DayOfWeek.MONDAY, new OXCount(0, 0)),
                countsByDay.getOrDefault(DayOfWeek.TUESDAY, new OXCount(0, 0)),
                countsByDay.getOrDefault(DayOfWeek.WEDNESDAY, new OXCount(0, 0)),
                countsByDay.getOrDefault(DayOfWeek.THURSDAY, new OXCount(0, 0)),
                countsByDay.getOrDefault(DayOfWeek.FRIDAY, new OXCount(0, 0)),
                countsByDay.getOrDefault(DayOfWeek.SATURDAY, new OXCount(0, 0)),
                countsByDay.getOrDefault(DayOfWeek.SUNDAY, new OXCount(0, 0)),
                totalCounts,
                roundedAverage
        );
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

    public OptionTypeRecordTrend getOptionTypeRecordTrend(Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isOptionType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_OPTION.getMessage(), METHOD_NOT_RECORD_OPTION.getStatus());
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record);

        Map<DayOfWeek, List<RecordDetail>> recordsByDay = thisWeekRecord.stream()
                .collect(Collectors.groupingBy(
                        detail -> detail.getCreatedAt().getDayOfWeek()
                ));

        Map<OptionDetail, Long> optionFrequencyMap = new HashMap<>();
        String unit = recordUnitRepository.getByRecord(record).getUnit();

        DayRecord monday = createDayRecord(recordsByDay.get(DayOfWeek.MONDAY), optionFrequencyMap, unit);
        DayRecord tuesday = createDayRecord(recordsByDay.get(DayOfWeek.TUESDAY), optionFrequencyMap, unit);
        DayRecord wednesday = createDayRecord(recordsByDay.get(DayOfWeek.WEDNESDAY), optionFrequencyMap, unit);
        DayRecord thursday = createDayRecord(recordsByDay.get(DayOfWeek.THURSDAY), optionFrequencyMap, unit);
        DayRecord friday = createDayRecord(recordsByDay.get(DayOfWeek.FRIDAY), optionFrequencyMap, unit);
        DayRecord saturday = createDayRecord(recordsByDay.get(DayOfWeek.SATURDAY), optionFrequencyMap, unit);
        DayRecord sunday = createDayRecord(recordsByDay.get(DayOfWeek.SUNDAY), optionFrequencyMap, unit);

        int totalCount = thisWeekRecord.size();
        OptionDetail mostFrequentOption = getMostFrequentOption(optionFrequencyMap);

        return OptionTypeRecordTrend.of(
                monday, tuesday, wednesday, thursday, friday, saturday, sunday, totalCount, mostFrequentOption
        );
    }

    private DayRecord createDayRecord(
            List<RecordDetail> records,
            Map<OptionDetail, Long> optionFrequencyMap,
            String unit
    ) {
        if (records == null || records.isEmpty()) {
            return new DayRecord(0, Collections.emptyList());
        }

        List<OptionDetail> optionDetails = records.stream()
                .flatMap(record -> record.getRecordDetailOptions().stream())
                .map(option -> {
                    RecordOption recordOption = option.getRecordOption();
                    return new OptionDetail(recordOption.getId(), recordOption.getOption() + unit);
                })
                .collect(Collectors.toList());

        // 옵션 빈도 업데이트
        for (OptionDetail option : optionDetails) {
            optionFrequencyMap.put(option, optionFrequencyMap.getOrDefault(option, 0L) + 1);
        }

        return new DayRecord(optionDetails.size(), optionDetails);
    }

    private OptionDetail getMostFrequentOption(Map<OptionDetail, Long> optionFrequencyMap) {
        return optionFrequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(new OptionDetail(null, "None"));
    }

    public TextTypeRecordTrend getTextTypeRecordTrend(Integer recordId) {
        UserRecord record = recordRepository.getById(recordId);
        if (!isTextType(record.getMethod())) {
            throw new GlobalException(METHOD_NOT_RECORD_TEXT.getMessage(), METHOD_NOT_RECORD_TEXT.getStatus());
        }

        List<RecordDetail> thisWeekRecord = findThisWeekRecord(record);
        Map<DayOfWeek, Long> countsByDay = getCountsByDayOfWeek(thisWeekRecord);
        int totalCount = thisWeekRecord.size();
        double averageCount = totalCount / 7.0;
        int roundedAverage = (int) Math.round(averageCount);

        return TextTypeRecordTrend.of(
                countsByDay.getOrDefault(DayOfWeek.MONDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.TUESDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.WEDNESDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.THURSDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.FRIDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.SATURDAY, 0L).intValue(),
                countsByDay.getOrDefault(DayOfWeek.SUNDAY, 0L).intValue(),
                totalCount,
                roundedAverage
        );
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

        System.out.println("기록 요약 prompt 내용: \n" + prompt);

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
}
