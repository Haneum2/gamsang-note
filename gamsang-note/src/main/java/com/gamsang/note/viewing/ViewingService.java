package com.gamsang.note.viewing;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gamsang.note.user.UserRepository;
import com.gamsang.note.viewing.ViewingDtos.CreateRecordRequest;
import com.gamsang.note.viewing.ViewingDtos.CreateReviewRequest;
import com.gamsang.note.viewing.ViewingDtos.RecordResponse;
import com.gamsang.note.viewing.ViewingDtos.ReviewResponse;
import com.gamsang.note.viewing.ViewingDtos.UpdateRecordRequest;
import com.gamsang.note.viewing.ViewingDtos.VisibleReviewResponse;
import com.gamsang.note.work.Work;
import com.gamsang.note.work.WorkRepository;

/**
 * 감상 기록·후기 로직. 메서드 하나가 트랜잭션 하나다:
 * 중간에 예외가 나면 그 메서드 안에서 한 DB 변경은 모두 취소(rollback)된다.
 */
@Service
@Transactional
public class ViewingService {

    private final ViewingRecordRepository recordRepository;
    private final ReviewRepository reviewRepository;
    private final VisibleReviewRepository visibleReviewRepository;
    private final WorkRepository workRepository;
    private final UserRepository userRepository;

    public ViewingService(ViewingRecordRepository recordRepository, ReviewRepository reviewRepository,
                          VisibleReviewRepository visibleReviewRepository, WorkRepository workRepository,
                          UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.reviewRepository = reviewRepository;
        this.visibleReviewRepository = visibleReviewRepository;
        this.workRepository = workRepository;
        this.userRepository = userRepository;
    }

    public RecordResponse createRecord(CreateRecordRequest req) {
        if (req.userId() == null || req.workId() == null) {
            throw bad("userId 와 workId 는 필수입니다.");
        }
        validateRating(req.rating());
        if (!userRepository.existsById(req.userId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.");
        }
        Work work = workRepository.findById(req.workId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "작품을 찾을 수 없습니다."));
        // DB 에도 UNIQUE(user_id, work_id) 가 있지만, 먼저 확인해서 알아보기 쉬운 오류(409)를 돌려준다.
        if (recordRepository.existsByUserIdAndWorkId(req.userId(), req.workId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 기록한 작품입니다.");
        }
        RecordStatus status = (req.status() == null) ? RecordStatus.WANT : req.status();
        ViewingRecord saved = recordRepository.save(
                new ViewingRecord(req.userId(), work, status, req.rating(), req.watchedDate()));
        return RecordResponse.from(saved);
    }

    public RecordResponse updateRecord(Long recordId, UpdateRecordRequest req) {
        validateRating(req.rating());
        ViewingRecord record = findOwnedRecord(recordId, req.userId());
        // save() 를 부르지 않아도 된다. 트랜잭션이 끝날 때 바뀐 필드를 감지해 UPDATE 를 실행한다.
        record.update(req.status(), req.rating(), req.watchedDate());
        return RecordResponse.from(record);
    }

    @Transactional(readOnly = true)
    public Page<RecordResponse> listRecords(Long userId, RecordStatus status, Pageable pageable) {
        if (userId == null) {
            throw bad("userId 는 필수입니다.");
        }
        return recordRepository.findMine(userId, status, pageable).map(RecordResponse::from);
    }

    public ReviewResponse createReview(Long recordId, CreateReviewRequest req) {
        if (req.content() == null || req.content().isBlank()) {
            throw bad("후기 내용을 입력해 주세요.");
        }
        findOwnedRecord(recordId, req.userId());
        Visibility visibility = (req.visibility() == null) ? Visibility.PRIVATE : req.visibility();
        boolean spoiler = Boolean.TRUE.equals(req.spoiler());
        // 후기를 저장하면 DB 트리거(trg_review_mark_done)가 해당 기록의 상태를 '봤어요'로 바꾼다.
        Review saved = reviewRepository.save(new Review(recordId, req.content().trim(), spoiler, visibility));
        return ReviewResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<VisibleReviewResponse> listVisibleReviews(Long viewerId, Pageable pageable) {
        if (viewerId == null) {
            throw bad("viewerId 는 필수입니다.");
        }
        return visibleReviewRepository.findByViewerId(viewerId, pageable).map(VisibleReviewResponse::from);
    }

    // ---- 내부 도우미 ----
    private ViewingRecord findOwnedRecord(Long recordId, Long userId) {
        if (userId == null) {
            throw bad("userId 는 필수입니다.");
        }
        ViewingRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "기록을 찾을 수 없습니다."));
        if (!record.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 기록만 수정할 수 있습니다.");
        }
        return record;
    }

    // DB 의 CHECK 제약(0.5~5.0, 0.5 단위)과 같은 규칙을 앱에서도 먼저 검사한다.
    private void validateRating(BigDecimal rating) {
        if (rating == null) {
            return;
        }
        boolean inRange = rating.compareTo(new BigDecimal("0.5")) >= 0 && rating.compareTo(new BigDecimal("5.0")) <= 0;
        boolean halfStep = rating.multiply(BigDecimal.TEN).remainder(BigDecimal.valueOf(5)).signum() == 0;
        if (!inRange || !halfStep) {
            throw bad("별점은 0.5~5.0 사이의 0.5 단위여야 합니다.");
        }
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
