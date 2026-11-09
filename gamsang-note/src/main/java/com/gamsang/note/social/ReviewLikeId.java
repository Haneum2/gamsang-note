package com.gamsang.note.social;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReviewLikeId implements Serializable {

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "user_id")
    private Long userId;

    protected ReviewLikeId() {
    }

    public ReviewLikeId(Long reviewId, Long userId) {
        this.reviewId = reviewId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewLikeId that)) return false;
        return Objects.equals(reviewId, that.reviewId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, userId);
    }
}
