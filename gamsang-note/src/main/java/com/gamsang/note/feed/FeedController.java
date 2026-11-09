package com.gamsang.note.feed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedController {

    private final FriendFeedRepository feedRepository;

    public FeedController(FriendFeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    // GET /api/feed?viewerId=1&page=0&size=20 : 친구들의 최근 후기 (전체·친구 공개만, 최신순)
    @GetMapping("/api/feed")
    @Transactional(readOnly = true)
    public Page<FeedItem> feed(@RequestParam Long viewerId,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Order.desc("createdAt")));
        return feedRepository.findByViewerId(viewerId, pageable).map(f -> new FeedItem(
                f.getReviewId(), f.getFriendId(), f.getNickname(), f.getWorkId(), f.getWorkType(), f.getTitle(),
                f.getRating(), f.getContent(), f.isSpoiler(), f.getVisibility(), f.getCreatedAt()));
    }

    public record FeedItem(Long reviewId, Long friendId, String nickname, Long workId, String workType,
                           String title, BigDecimal rating, String content, boolean spoiler,
                           String visibility, LocalDateTime createdAt) {
    }
}
