package com.momenty.record.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "record_detail")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RecordDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content")
    private String content;

    @Column(name = "is_public")
    private Boolean isPublic = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private UserRecord record;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = true)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "recordDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordDetailOption> recordDetailOptions = new ArrayList<>();


    @Builder
    private RecordDetail(
            String content,
            UserRecord record,
            boolean isPublic,
            LocalDateTime createdAt
    ) {
        this.content = content;
        this.record = record;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
    }

    public void update(String content, Boolean isPublic, Integer hour, Integer minute) {
        if (content != null) {
            this.content = content;
        }
        if (isPublic != null) {
            this.isPublic = isPublic;
        }

        if (hour != null || minute != null) {
            int newHour = (hour != null) ? hour : this.createdAt.getHour();
            int newMinute = (minute != null) ? minute : this.createdAt.getMinute();
            this.createdAt = this.createdAt.withHour(newHour).withMinute(newMinute);
        }

    }
}
