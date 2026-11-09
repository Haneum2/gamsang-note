package com.gamsang.note.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.gamsang.note.viewing.RecordStatus;
import com.gamsang.note.viewing.ViewingRecord;

/**
 * 통계 전용 저장소. 엔티티 하나를 가져오는 게 아니라 집계(count, avg)를 하므로 결과를 Object[] 행으로 받는다.
 * Repository<T, ID> 만 상속하면 우리가 쓴 @Query 메서드만 가진 최소한의 저장소가 된다.
 */
public interface StatsRepository extends Repository<ViewingRecord, Long> {

    // 월별 '봤어요' 수: [월(1~12), 건수]
    @Query("""
            select extract(month from r.watchedDate), count(r)
            from ViewingRecord r
            where r.userId = :userId and r.status = :done
              and r.watchedDate >= :from and r.watchedDate < :to
            group by extract(month from r.watchedDate)
            order by extract(month from r.watchedDate)
            """)
    List<Object[]> countByMonth(@Param("userId") Long userId, @Param("done") RecordStatus done,
                                @Param("from") LocalDate from, @Param("to") LocalDate to);

    // 장르별 '봤어요' 수: [장르명, 건수] (감상일 범위로 거른다)
    @Query("""
            select g.name, count(r)
            from ViewingRecord r join r.work w join w.genres g
            where r.userId = :userId and r.status = :done
              and r.watchedDate >= :from and r.watchedDate < :to
            group by g.id, g.name
            order by count(r) desc, g.name
            """)
    List<Object[]> countByGenre(@Param("userId") Long userId, @Param("done") RecordStatus done,
                                @Param("from") LocalDate from, @Param("to") LocalDate to, Pageable pageable);

    // 장르별 평균 별점: [장르명, 평균] (별점을 준 기록만)
    @Query("""
            select g.name, avg(r.rating)
            from ViewingRecord r join r.work w join w.genres g
            where r.userId = :userId and r.rating is not null
            group by g.id, g.name
            """)
    List<Object[]> avgRatingByGenre(@Param("userId") Long userId);

    // 종류별 '봤어요' 수: [WorkType, 건수]
    @Query("""
            select w.type, count(r)
            from ViewingRecord r join r.work w
            where r.userId = :userId and r.status = :done
              and r.watchedDate >= :from and r.watchedDate < :to
            group by w.type
            order by count(r) desc
            """)
    List<Object[]> countByType(@Param("userId") Long userId, @Param("done") RecordStatus done,
                               @Param("from") LocalDate from, @Param("to") LocalDate to);

    // 별점 높은 기록 (Pageable 로 상위 N건만)
    @Query("""
            select r from ViewingRecord r join fetch r.work w
            where r.userId = :userId and r.status = :done and r.rating is not null
              and r.watchedDate >= :from and r.watchedDate < :to
            order by r.rating desc, r.watchedDate
            """)
    List<ViewingRecord> topRated(@Param("userId") Long userId, @Param("done") RecordStatus done,
                                 @Param("from") LocalDate from, @Param("to") LocalDate to, Pageable pageable);
}
