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

import com.gamsang.note.work.WorkDtos.ExternalItem;
import com.gamsang.note.work.WorkDtos.WorkDetail;
import com.gamsang.note.work.WorkDtos.WorkSummary;

@RestController
@RequestMapping("/api/works")
public class WorkController {

    private final WorkService workService;
    private final KopisService kopisService;
    private final TmdbService tmdbService;
    private final Yes24Service yes24Service;

    public WorkController(WorkService workService, KopisService kopisService, TmdbService tmdbService,
                          Yes24Service yes24Service) {
        this.workService = workService;
        this.kopisService = kopisService;
        this.tmdbService = tmdbService;
        this.yes24Service = yes24Service;
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
    public List<ExternalItem> searchKopis(@RequestParam(defaultValue = "") String q,
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

    // GET /api/works/tmdb?q=기생충&page=0  →  TMDB 영화 검색(20건씩)
    @GetMapping("/tmdb")
    public List<ExternalItem> searchTmdb(@RequestParam(defaultValue = "") String q,
                                         @RequestParam(defaultValue = "0") int page) {
        return tmdbService.search(q.trim(), Math.max(page, 0));
    }

    // POST /api/works/tmdb/496243  →  우리 DB에 저장하고 작품 상세를 돌려준다(이미 있으면 그대로)
    @PostMapping("/tmdb/{externalId}")
    public WorkDetail importTmdb(@PathVariable String externalId) {
        if (!externalId.matches("\\d{1,10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TMDB 영화 ID 형식이 아닙니다.");
        }
        return workService.get(tmdbService.importMovie(externalId));
    }

    // GET /api/works/yes24?q=소년이 온다&page=0  →  YES24 도서 검색(20건씩, 세트 상품 제외)
    @GetMapping("/yes24")
    public List<ExternalItem> searchYes24(@RequestParam(defaultValue = "") String q,
                                          @RequestParam(defaultValue = "0") int page) {
        return yes24Service.search(q.trim(), Math.max(page, 0));
    }

    // POST /api/works/yes24/13137546  →  YES24 상품 번호로 저장하고 작품 상세를 돌려준다(같은 ISBN 이 있으면 그 작품)
    @PostMapping("/yes24/{externalId}")
    public WorkDetail importYes24(@PathVariable String externalId) {
        if (!externalId.matches("\\d{1,12}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "YES24 상품 번호 형식이 아닙니다.");
        }
        return workService.get(yes24Service.importBook(externalId));
    }
}
