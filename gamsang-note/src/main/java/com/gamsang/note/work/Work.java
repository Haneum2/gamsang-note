package com.gamsang.note.work;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.annotations.BatchSize;

import com.gamsang.note.genre.Genre;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/** work 테이블(슈퍼타입). 영화·책·공연의 공통 정보를 가진다. */
@Entity
@Table(name = "work")
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long id;

    // DB에는 문자열로 저장돼 있으므로 STRING 으로 매핑한다. (ORDINAL 은 순서 번호라 쓰면 안 된다)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 12)
    private WorkType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "summary")
    private String summary;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    /**
     * N:M 관계를 연결 테이블 work_genre 로 풀어서 매핑한다.
     * LAZY(기본): 장르는 실제로 꺼내 쓸 때 조회한다.
     * BatchSize: 작품 여러 건의 장르를 한 번에 묶어서(최대 50건씩) 조회해 쿼리 수를 줄인다.
     */
    @ManyToMany
    @JoinTable(name = "work_genre",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @BatchSize(size = 50)
    private Set<Genre> genres = new LinkedHashSet<>();

    protected Work() {
    }

    public Long getId() { return id; }
    public WorkType getType() { return type; }
    public String getTitle() { return title; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getSummary() { return summary; }
    public String getPosterUrl() { return posterUrl; }
    public Set<Genre> getGenres() { return genres; }
}
