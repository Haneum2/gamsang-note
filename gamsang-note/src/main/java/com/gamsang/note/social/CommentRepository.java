package com.gamsang.note.social;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByReviewIdOrderByIdAsc(Long reviewId);

    long countByReviewId(Long reviewId);
}
