package com.gamsang.note.viewing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gamsang.note.work.WorkType;

public final class ViewingDtos {

    private ViewingDtos() {
    }

    // ---- 요청 ----
    // userId 는 로그인 기능을 붙이기 전까지 임시로 요청에 담아 보낸다. (나중에 로그인 정보로 대체)
    public record CreateRecordRequest(Long userId, Long workId, RecordStatus status,
                                      BigDecimal rating, LocalDate watchedDate) {
    }

    public record UpdateRecordRequest(Long userId, RecordStatus status,
                                      BigDecimal rating, LocalDate watchedDate) {
    }

    public record CreateReviewRequest(Long userId, String content, Boolean spoiler, Visibility visibility) {
    }

    // ---- 응답 ----
    public record RecordResponse(Long id, Long workId, WorkType workType, String title,
                                 RecordStatus status, BigDecimal rating, LocalDate watchedDate) {
        static RecordResponse from(ViewingRecord r) {
            return new RecordResponse(r.getId(), r.getWork().getId(), r.getWork().getType(),
                    r.getWork().getTitle(), r.getStatus(), r.getRating(), r.getWatchedDate());
        }
    }

    public record ReviewResponse(Long id, Long recordId, String content, boolean spoiler,
                                 Visibility visibility, LocalDateTime createdAt) {
        static ReviewResponse from(Review v) {
            return new ReviewResponse(v.getId(), v.getRecordId(), v.getContent(), v.isSpoiler(),
                    v.getVisibility(), v.getCreatedAt());
        }
    }

    public record VisibleReviewResponse(Long reviewId, Long authorId, String nickname, Long workId,
                                        String workType, String title, BigDecimal rating, String content,
                                        boolean spoiler, String visibility, LocalDateTime createdAt) {
        static VisibleReviewResponse from(VisibleReview v) {
            return new VisibleReviewResponse(v.getReviewId(), v.getAuthorId(), v.getNickname(), v.getWorkId(),
                    v.getWorkType(), v.getTitle(), v.getRating(), v.getContent(), v.isSpoiler(),
                    v.getVisibility(), v.getCreatedAt());
        }
    }
}
