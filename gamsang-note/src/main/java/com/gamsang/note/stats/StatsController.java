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
import com.gamsang.note.user.LoginUser;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // GET /api/stats/monthly?year=2026  : 내 월별 감상 수 (12개월)
    @GetMapping("/monthly")
    public List<MonthlyCount> monthly(@LoginUser Long me,
                                      @RequestParam(required = false) Integer year) {
        return statsService.monthly(me, year != null ? year : LocalDate.now().getYear());
    }

    // GET /api/stats/genres  : 내 장르별 감상 수·비율·평균 별점
    @GetMapping("/genres")
    public List<GenreStat> genres(@LoginUser Long me) {
        return statsService.genres(me);
    }

    // GET /api/stats/year-end?year=2026  : 내 연말 결산
    @GetMapping("/year-end")
    public YearEnd yearEnd(@LoginUser Long me,
                           @RequestParam(required = false) Integer year) {
        return statsService.yearEnd(me, year != null ? year : LocalDate.now().getYear());
    }
}
