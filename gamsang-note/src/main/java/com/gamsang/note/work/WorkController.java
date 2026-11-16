package com.gamsang.note.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gamsang.note.work.WorkDtos.KopisItem;
import com.gamsang.note.work.WorkDtos.WorkDetail;
import com.gamsang.note.work.WorkDtos.WorkSummary;

@RestController
@RequestMapping("/api/works")
public class WorkController {

    private final WorkService workService;
    private final KopisService kopisService;

    public WorkController(WorkService workService, KopisService kopisService) {
        this.workService = workService;
        this.kopisService = kopisService;
    }

    // GET /api/works?type=MOVIE&q=왕&page=0&size=20
    //   type, q 는 생략 가능. 제목순으로 정렬해서 page(0부터) 단위로 돌려준다.
    @GetMapping
    public Page<WorkSummary> search(@RequestParam(required = false) WorkType type,
                                    @RequestParam(required = false) String q,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return workService.search(type, q, PageRequest.of(Math.max(page, 0), safeSize, Sort.by("title")));
    }

    // GET /api/works/1  →  작품 상세
    @GetMapping("/{id}")
    public WorkDetail get(@PathVariable Long id) {
        return workService.get(id);
    }

    // GET /api/works/kopis?q=햄릿&page=0  →  KOPIS 공연 검색(오늘 ±15일에 걸친 공연, 20건씩)
    //   경로 "kopis" 는 글자 그대로라 "/{id}" 보다 먼저 맞춰진다.
    @GetMapping("/kopis")
    public List<KopisItem> searchKopis(@RequestParam(defaultValue = "") String q,
                                       @RequestParam(defaultValue = "0") int page) {
        return kopisService.search(q.trim(), Math.max(page, 0));
    }

    // POST /api/works/kopis/PF304730  →  우리 DB에 저장하고 작품 상세를 돌려준다(이미 있으면 그대로)
    @PostMapping("/kopis/{externalId}")
    public WorkDetail importKopis(@PathVariable String externalId) {
        if (!externalId.matches("PF\\d{1,10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "KOPIS 공연ID 형식이 아닙니다.");
        }
        return workService.get(kopisService.importPerformance(externalId));
    }
}
