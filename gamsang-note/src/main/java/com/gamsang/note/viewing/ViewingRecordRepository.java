package com.gamsang.note.viewing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewingRecordRepository extends JpaRepository<ViewingRecord, Long> {

    // 메서드 이름만으로 쿼리가 만들어진다: where user_id = ? and work_id = ?
    boolean existsByUserIdAndWorkId(Long userId, Long workId);

    // @EntityGraph: 목록에서 작품 제목을 함께 보여 주므로 work 를 join 해서 한 번에 가져온다(N+1 방지).
    // 정렬을 JPQL 에 직접 쓴 이유: Oracle 은 DESC 에서 NULL 을 맨 앞에 두는데,
    // 메서드 이름 쿼리 + Sort.nullsLast() 는 SQL 에 반영되지 않았다. status 가 null 이면 전체.
    @EntityGraph(attributePaths = "work")
    @Query(value = """
            select r from ViewingRecord r
            where r.userId = :userId and (:status is null or r.status = :status)
            order by r.watchedDate desc nulls last, r.id desc""",
           countQuery = "select count(r) from ViewingRecord r where r.userId = :userId and (:status is null or r.status = :status)")
    Page<ViewingRecord> findMine(@Param("userId") Long userId, @Param("status") RecordStatus status, Pageable pageable);
}
