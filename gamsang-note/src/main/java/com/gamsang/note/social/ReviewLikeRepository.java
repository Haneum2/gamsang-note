package com.gamsang.note.social;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

    @Query("select count(l) from ReviewLike l where l.id.reviewId = :reviewId")
    long countByReview(@Param("reviewId") Long reviewId);
}
