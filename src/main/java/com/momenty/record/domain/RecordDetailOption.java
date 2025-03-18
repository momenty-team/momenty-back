package com.momenty.record.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "record_detail_option")
@NoArgsConstructor(access = PROTECTED)
public class RecordDetailOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_detail_id", nullable = false)
    private RecordDetail recordDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_option_id", nullable = false)
    private RecordOption recordOption;

    @Builder
    private RecordDetailOption(
            RecordDetail recordDetail,
            RecordOption recordOption
    ) {
        this.recordDetail = recordDetail;
        this.recordOption = recordOption;
    }
}
