package com.gamsang.note.social;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** review_like 연결 테이블(회원 N:M 후기). 한 회원은 후기당 좋아요를 한 번만 누른다(기본키가 막는다). */
@Entity
@Table(name = "review_like")
public class ReviewLike {

    @EmbeddedId
    private ReviewLikeId id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ReviewLike() {
    }

    public ReviewLike(Long reviewId, Long userId) {
        this.id = new ReviewLikeId(reviewId, userId);
    }

    public ReviewLikeId getId() { return id; }
}
