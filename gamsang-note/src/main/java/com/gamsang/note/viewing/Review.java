package com.gamsang.note.viewing;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.type.YesNoConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "content", nullable = false)
    private String content;

    // DB 에는 'Y'/'N' (CHAR(1)), 자바에서는 boolean. YesNoConverter 가 변환해 준다.
    @Convert(converter = YesNoConverter.class)
    @Column(name = "spoiler_yn", nullable = false, length = 1)
    private boolean spoiler;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10)
    private Visibility visibility;

    // 저장할 때 현재 시각을 채워 준다.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Review() {
    }

    public Review(Long recordId, String content, boolean spoiler, Visibility visibility) {
        this.recordId = recordId;
        this.content = content;
        this.spoiler = spoiler;
        this.visibility = visibility;
    }

    public Long getId() { return id; }
    public Long getRecordId() { return recordId; }
    public String getContent() { return content; }
    public boolean isSpoiler() { return spoiler; }
    public Visibility getVisibility() { return visibility; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
