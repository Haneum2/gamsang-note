package com.gamsang.note.viewing;

import java.io.Serializable;
import java.util.Objects;

/** v_visible_review 뷰는 (보는 사람, 후기) 한 쌍이 한 행이므로 기본키가 두 컬럼이다. */
public class VisibleReviewId implements Serializable {

    private Long viewerId;
    private Long reviewId;

    public VisibleReviewId() {
    }

    public VisibleReviewId(Long viewerId, Long reviewId) {
        this.viewerId = viewerId;
        this.reviewId = reviewId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisibleReviewId that)) return false;
        return Objects.equals(viewerId, that.viewerId) && Objects.equals(reviewId, that.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(viewerId, reviewId);
    }
}
