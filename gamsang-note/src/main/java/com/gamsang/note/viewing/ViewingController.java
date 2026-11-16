package com.gamsang.note.viewing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gamsang.note.viewing.ViewingDtos.CreateRecordRequest;
import com.gamsang.note.viewing.ViewingDtos.CreateReviewRequest;
import com.gamsang.note.viewing.ViewingDtos.RecordResponse;
import com.gamsang.note.viewing.ViewingDtos.ReviewResponse;
import com.gamsang.note.viewing.ViewingDtos.UpdateRecordRequest;
import com.gamsang.note.viewing.ViewingDtos.VisibleReviewResponse;
import com.gamsang.note.user.LoginUser;

@RestController
public class ViewingController {

    private final ViewingService viewingService;

    public ViewingController(ViewingService viewingService) {
        this.viewingService = viewingService;
    }

    // POST /api/records  : 감상 기록 등록
    @PostMapping("/api/records")
    @ResponseStatus(HttpStatus.CREATED)
    public RecordResponse createRecord(@LoginUser Long me, @RequestBody CreateRecordRequest request) {
        return viewingService.createRecord(me, request);
    }

    // PATCH /api/records/{id}  : 상태·별점·감상일 수정 (보낸 값만 바뀐다)
    @PatchMapping("/api/records/{id}")
    public RecordResponse updateRecord(@LoginUser Long me, @PathVariable Long id,
                                       @RequestBody UpdateRecordRequest request) {
        return viewingService.updateRecord(id, me, request);
    }

    // GET /api/records?status=DONE&page=0&size=20  : 내 감상 기록 목록 (감상일 최신순, 없는 건 뒤로)
    @GetMapping("/api/records")
    public Page<RecordResponse> listRecords(@LoginUser Long me,
                                            @RequestParam(required = false) RecordStatus status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        // 정렬(감상일 최신순, 감상일 없는 기록은 뒤로)은 리포지토리 쿼리에 들어 있다.
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        return viewingService.listRecords(me, status, pageable);
    }

    // POST /api/records/{id}/review  : 후기 작성 (공개 범위 포함)
    @PostMapping("/api/records/{id}/review")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(@LoginUser Long me, @PathVariable Long id,
                                       @RequestBody CreateReviewRequest request) {
        return viewingService.createReview(id, me, request);
    }

    // GET /api/reviews  : 내가 볼 수 있는 후기 목록 (공개 범위 적용, 최신순)
    @GetMapping("/api/reviews")
    public Page<VisibleReviewResponse> listReviews(@LoginUser Long me,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Order.desc("createdAt")));
        return viewingService.listVisibleReviews(me, pageable);
    }
}
