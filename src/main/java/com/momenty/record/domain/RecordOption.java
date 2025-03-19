package com.momenty.record.domain;

import static lombok.AccessLevel.PROTECTED;

import com.momenty.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "record_option")
@NoArgsConstructor(access = PROTECTED)
public class RecordOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Column(name = "option_value", length = 255)
    private String option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private UserRecord record;

    @OneToMany(mappedBy = "recordOption", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordDetailOption> recordDetailOptions = new ArrayList<>();


    @Builder
    private RecordOption(
            String option,
            User user,
            UserRecord userRecord
    ) {
        this.option = option;
        this.user = user;
        this.record = userRecord;
    }

    public void addUnit(String unit) {
        this.option = this.option + unit;
    }

    public void addOption(String option) {
        this.option = option;
    }
}
