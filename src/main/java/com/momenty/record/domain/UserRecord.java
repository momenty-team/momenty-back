package com.momenty.record.domain;

import static lombok.AccessLevel.PROTECTED;

import com.momenty.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "record")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Column(name = "title", length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    private RecordMethod method;

    @Column(name = "is_public")
    private boolean isPublic = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "userRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private RecordUnit recordUnit;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordOption> recordOptions = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordDetail> recordDetails = new ArrayList<>();

    @Builder
    private UserRecord(
            String title,
            RecordMethod method,
            Boolean isPublic,
            User user
    ) {
        this.title = title;
        this.method = method;
        this.isPublic = isPublic;
        this.user = user;
    }

    public void updateUnit(String unit) {
        this.recordUnit.updateUnit(unit);
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
