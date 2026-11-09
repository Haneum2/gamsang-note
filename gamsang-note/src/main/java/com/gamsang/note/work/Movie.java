package com.gamsang.note.work;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** movie 테이블(서브타입). 기본키 work_id 가 work 의 외래키이기도 하다. */
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @Column(name = "work_id")
    private Long workId;

    @Column(name = "runtime")
    private Integer runtime;

    protected Movie() {
    }

    public Long getWorkId() { return workId; }
    public Integer getRuntime() { return runtime; }
}
