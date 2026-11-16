package com.gamsang.note.viewing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gamsang.note.work.WorkType;

public final class ViewingDtos {

    private ViewingDtos() {
    }

    // ---- 요청 ----
    // 회원 번호는 요청에 담지 않는다. 컨트롤러가 로그인 세션에서 꺼내 서비스에 넘긴다(@LoginUser).
    public record CreateRecordRequest(Long workId, RecordStatus status,
                                      BigDecimal rating, LocalDate watchedDate) {
    }

    public record UpdateRecordRequest(RecordStatus status, BigDecimal rating, LocalDate watchedDate) {
    }

    public record CreateReviewRequest(String content, Boolean spoiler, Visibility visibility) {
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
