package com.momenty.notification.domain;

import com.momenty.notification.service.NotificationService;
import com.momenty.record.domain.UserRecord;
import com.momenty.record.domain.UserRecordAvgTime;
import com.momenty.record.repository.RecordDetailRepository;
import com.momenty.record.repository.UserRecordAvgTimeRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final UserRecordAvgTimeRepository avgTimeRepository;
    private final RecordDetailRepository recordDetailRepository;
    private final NotificationService notificationService;

    // 매시간 정각마다 실행
    @Scheduled(cron = "0 0 * * * ?")
    public void checkUserRecordsAndNotify() {
        int currentHour = LocalDateTime.now().getHour();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate = now;

        if (currentHour == 0) {
            // 00시일 경우: 이전 날짜의 05시 ~ 현재 시각(00시)
            startDate = now.minusDays(1).with(LocalTime.of(5, 0));
        } else {
            // 1시 ~ 23시일 경우: 오늘의 시작 시간(00시) ~ 현재 시각
            startDate = now.with(LocalTime.MIN);
        }

        List<UserRecordAvgTime> avgTimes = avgTimeRepository.findByAverageHour(currentHour);

        for (UserRecordAvgTime avgTime : avgTimes) {
            UserRecord record = avgTime.getRecord();
            Integer recordId = record.getId();
            Integer userId = avgTime.getUser().getId();

            boolean hasRecord = recordDetailRepository.existsByRecordIdAndCreatedAtBetween(recordId, startDate, endDate);

            if (!hasRecord) {
                notificationService.sendRecordNotification(userId, record);
            }
        }
    }
}
