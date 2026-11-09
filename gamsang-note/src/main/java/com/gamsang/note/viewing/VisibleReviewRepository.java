package com.gamsang.note.viewing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisibleReviewRepository extends JpaRepository<VisibleReview, VisibleReviewId> {

    Page<VisibleReview> findByViewerId(Long viewerId, Pageable pageable);
}
