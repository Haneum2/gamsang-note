package com.gamsang.note.viewing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;
import org.hibernate.type.YesNoConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * 03_views_triggers.sql 에서 만든 뷰 v_visible_review 를 읽기 전용 엔티티로 매핑했다.
 * 공개 범위(전체/친구/나만 보기) 판단은 뷰가 이미 하므로, 자바에서는 viewer_id 로 거르기만 하면 된다.
 */
@Entity
@Immutable
@Table(name = "v_visible_review")
@IdClass(VisibleReviewId.class)
public class VisibleReview {

    @Id
    @Column(name = "viewer_id")
    private Long viewerId;

    @Id
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "work_id")
    private Long workId;

    @Column(name = "work_type")
    private String workType;

    @Column(name = "title")
    private String title;

    @Column(name = "rating")
    private BigDecimal rating;

    @Lob
    @Column(name = "content")
    private String content;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "spoiler_yn")
    private boolean spoiler;

    @Column(name = "visibility")
    private String visibility;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected VisibleReview() {
    }

    public Long getViewerId() { return viewerId; }
    public Long getReviewId() { return reviewId; }
    public Long getAuthorId() { return authorId; }
    public String getNickname() { return nickname; }
    public Long getWorkId() { return workId; }
    public String getWorkType() { return workType; }
    public String getTitle() { return title; }
    public BigDecimal getRating() { return rating; }
    public String getContent() { return content; }
    public boolean isSpoiler() { return spoiler; }
    public String getVisibility() { return visibility; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
