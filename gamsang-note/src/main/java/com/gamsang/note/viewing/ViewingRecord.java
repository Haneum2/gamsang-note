package com.gamsang.note.viewing;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gamsang.note.work.Work;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * record 테이블(감상 기록). 이름이 Java 의 Record 와 겹치지 않도록 클래스 이름은 ViewingRecord 로 했다.
 */
@Entity
@Table(name = "record")
public class ViewingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // N:1 관계. 외래키 컬럼 work_id 를 Work 객체로 연결한다. LAZY: 필요할 때만 작품을 조회한다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private RecordStatus status;

    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "watched_date")
    private LocalDate watchedDate;

    protected ViewingRecord() {
    }

    public ViewingRecord(Long userId, Work work, RecordStatus status, BigDecimal rating, LocalDate watchedDate) {
        this.userId = userId;
        this.work = work;
        this.status = status;
        this.rating = rating;
        this.watchedDate = watchedDate;
    }

    /** null 로 들어온 값은 바꾸지 않는다. 트랜잭션 안에서 필드만 바꾸면 JPA 가 UPDATE 를 자동으로 실행한다(변경 감지). */
    public void update(RecordStatus status, BigDecimal rating, LocalDate watchedDate) {
        if (status != null) this.status = status;
        if (rating != null) this.rating = rating;
        if (watchedDate != null) this.watchedDate = watchedDate;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Work getWork() { return work; }
    public RecordStatus getStatus() { return status; }
    public BigDecimal getRating() { return rating; }
    public LocalDate getWatchedDate() { return watchedDate; }
}
