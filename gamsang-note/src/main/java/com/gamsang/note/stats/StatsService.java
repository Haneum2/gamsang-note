package com.gamsang.note.stats;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamsang.note.stats.StatsDtos.GenreCount;
import com.gamsang.note.stats.StatsDtos.GenreStat;
import com.gamsang.note.stats.StatsDtos.MonthlyCount;
import com.gamsang.note.stats.StatsDtos.TopWork;
import com.gamsang.note.stats.StatsDtos.TypeCount;
import com.gamsang.note.stats.StatsDtos.YearEnd;
import com.gamsang.note.viewing.RecordStatus;
import com.gamsang.note.work.WorkType;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private static final LocalDate MIN_DATE = LocalDate.of(1900, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(2999, 12, 31);

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    /** 1~12월을 모두 채워서 돌려준다(없는 달은 0). 차트에 그리기 편하다. */
    public List<MonthlyCount> monthly(Long userId, int year) {
        Map<Integer, Long> byMonth = new HashMap<>();
        for (Object[] row : statsRepository.countByMonth(userId, RecordStatus.DONE, start(year), start(year + 1))) {
            byMonth.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        List<MonthlyCount> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            result.add(new MonthlyCount(m, byMonth.getOrDefault(m, 0L)));
        }
        return result;
    }

    /** 장르별 감상 수·비율·평균 별점 (전체 기간) */
    public List<GenreStat> genres(Long userId) {
        List<Object[]> counts = statsRepository.countByGenre(userId, RecordStatus.DONE, MIN_DATE, MAX_DATE,
                Pageable.unpaged());
        Map<String, Double> avg = new HashMap<>();
        for (Object[] row : statsRepository.avgRatingByGenre(userId)) {
            avg.put((String) row[0], row[1] == null ? null : round(((Number) row[1]).doubleValue(), 2));
        }
        long total = counts.stream().mapToLong(r -> ((Number) r[1]).longValue()).sum();
        List<GenreStat> result = new ArrayList<>();
        for (Object[] row : counts) {
            String name = (String) row[0];
            long cnt = ((Number) row[1]).longValue();
            double pct = total == 0 ? 0 : round(cnt * 100.0 / total, 1);
            result.add(new GenreStat(name, cnt, pct, avg.get(name)));
        }
        return result;
    }

    /** 연말 결산: 본 작품 수, 종류별 수, 선호 장르 TOP3, 최고 평점 TOP5 */
    public YearEnd yearEnd(Long userId, int year) {
        LocalDate from = start(year);
        LocalDate to = start(year + 1);

        List<TypeCount> byType = new ArrayList<>();
        long total = 0;
        for (Object[] row : statsRepository.countByType(userId, RecordStatus.DONE, from, to)) {
            long cnt = ((Number) row[1]).longValue();
            byType.add(new TypeCount((WorkType) row[0], cnt));
            total += cnt;
        }

        List<GenreCount> topGenres = statsRepository
                .countByGenre(userId, RecordStatus.DONE, from, to, PageRequest.of(0, 3)).stream()
                .map(r -> new GenreCount((String) r[0], ((Number) r[1]).longValue()))
                .toList();

        List<TopWork> topRated = statsRepository
                .topRated(userId, RecordStatus.DONE, from, to, PageRequest.of(0, 5)).stream()
                .map(r -> new TopWork(r.getWork().getId(), r.getWork().getTitle(), r.getWork().getType(),
                        r.getRating(), r.getWatchedDate()))
                .toList();

        return new YearEnd(year, total, byType, topGenres, topRated);
    }

    private LocalDate start(int year) {
        return LocalDate.of(year, 1, 1);
    }

    private double round(double v, int scale) {
        double f = Math.pow(10, scale);
        return Math.round(v * f) / f;
    }
}
