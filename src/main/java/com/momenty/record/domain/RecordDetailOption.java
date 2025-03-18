package com.momenty.record.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "record_detail_option")
@NoArgsConstructor(access = PROTECTED)
public class RecordDetailOption {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_detail_id", nullable = false)
    private RecordDetail recordDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_option_id", nullable = false)
    private RecordOption recordOption;
}
