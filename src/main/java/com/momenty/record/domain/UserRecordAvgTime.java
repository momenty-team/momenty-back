package com.momenty.record.domain;

import static lombok.AccessLevel.PROTECTED;

import com.momenty.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "user_record_avg_time")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserRecordAvgTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false)
    private UserRecord record;

    @Column(name = "average_hour", nullable = false)
    private Integer averageHour;

    @Column(name = "recent_hours", nullable = false)
    private String recentHours = "";

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private static final int MAX_RECENT_RECORDS = 5;

    public UserRecordAvgTime(User user, UserRecord record, Integer initialHour) {
        this.user = user;
        this.record = record;
        this.averageHour = (initialHour + 1) % 24;
        this.recentHours = String.valueOf((initialHour + 1) % 24);
    }

    public void updateAverageHour(Integer newHour) {
        int adjustedHour = (newHour + 1) % 24;
        updateRecentHours(adjustedHour);
        this.averageHour = calculateRecentAverage();
    }

    private void updateRecentHours(int newHour) {
        List<Integer> recentList = Arrays.stream(recentHours.split(","))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        if (recentList.size() >= MAX_RECENT_RECORDS) {
            recentList.remove(0);  // 가장 오래된 기록 제거
        }

        recentList.add(newHour);

        this.recentHours = recentList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private int calculateRecentAverage() {
        List<Integer> recentList = Arrays.stream(recentHours.split(","))
                .map(Integer::parseInt)
                .toList();

        int sum = recentList.stream().mapToInt(Integer::intValue).sum();
        return sum / recentList.size();
    }
}
