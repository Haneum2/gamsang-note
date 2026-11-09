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

    public Long getWorkId() { return workId; }
    public String getVenue() { return venue; }
    public String getPeriod() { return period; }
}
