package com.gamsang.note.work;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamsang.note.work.WorkDtos.WorkDetail;
import com.gamsang.note.work.WorkDtos.WorkSummary;

@RestController
@RequestMapping("/api/works")
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
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
}
