package com.gamsang.note.work;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "performance")
public class Performance {

    @Id
    @Column(name = "work_id")
    private Long workId;

    @Column(name = "venue", length = 100)
    private String venue;

    @Column(name = "period", length = 100)
    private String period;

    protected Performance() {
    }

    // 기본키를 직접 넣는다(슈퍼타입 work 의 work_id 를 그대로 쓰는 PK=FK 구조).
    public Performance(Long workId, String venue, String period) {
        this.workId = workId;
        this.venue = venue;
        this.period = period;
    }

    public Long getWorkId() { return workId; }
    public String getVenue() { return venue; }
    public String getPeriod() { return period; }
}
