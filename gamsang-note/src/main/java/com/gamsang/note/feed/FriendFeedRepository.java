package com.gamsang.note.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gamsang.note.viewing.VisibleReviewId;

public interface FriendFeedRepository extends JpaRepository<FriendFeed, VisibleReviewId> {

    Page<FriendFeed> findByViewerId(Long viewerId, Pageable pageable);
}
