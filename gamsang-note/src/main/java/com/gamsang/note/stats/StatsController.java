package com.gamsang.note.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamsang.note.stats.StatsDtos.GenreStat;
import com.gamsang.note.stats.StatsDtos.MonthlyCount;
import com.gamsang.note.stats.StatsDtos.YearEnd;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // GET /api/stats/monthly?userId=1&year=2026  : 월별 감상 수 (12개월)
    @GetMapping("/monthly")
    public List<MonthlyCount> monthly(@RequestParam Long userId,
                                      @RequestParam(required = false) Integer year) {
        return statsService.monthly(userId, year != null ? year : LocalDate.now().getYear());
    }

    // GET /api/stats/genres?userId=1  : 장르별 감상 수·비율·평균 별점
    @GetMapping("/genres")
    public List<GenreStat> genres(@RequestParam Long userId) {
        return statsService.genres(userId);
    }

    // GET /api/stats/year-end?userId=1&year=2026  : 연말 결산
    @GetMapping("/year-end")
    public YearEnd yearEnd(@RequestParam Long userId,
                           @RequestParam(required = false) Integer year) {
        return statsService.yearEnd(userId, year != null ? year : LocalDate.now().getYear());
    }
}
