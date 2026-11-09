package com.gamsang.note.stats;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.gamsang.note.work.WorkType;

public final class StatsDtos {

    private StatsDtos() {
    }

    public record MonthlyCount(int month, long count) {
    }

    /** percent: 전체 '장르 태그' 중 이 장르 비율(%). 한 작품이 장르를 여러 개 가지면 장르마다 1건으로 센다. */
    public record GenreStat(String genre, long count, double percent, Double avgRating) {
    }

    public record TypeCount(WorkType type, long count) {
    }

    public record GenreCount(String genre, long count) {
    }

    public record TopWork(Long workId, String title, WorkType type, BigDecimal rating, LocalDate watchedDate) {
    }

    public record YearEnd(int year, long totalDone, List<TypeCount> byType,
                          List<GenreCount> topGenres, List<TopWork> topRated) {
    }
}
