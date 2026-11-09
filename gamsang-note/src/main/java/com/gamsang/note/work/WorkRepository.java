package com.gamsang.note.work;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkRepository extends JpaRepository<Work, Long> {

    /**
     * 종류와 제목 일부로 검색한다. (JPQL: 테이블이 아니라 엔티티·필드 이름으로 쓰는 쿼리)
     * 페이징(Pageable)을 주면 Spring 이 Oracle 용 FETCH FIRST 쿼리와 전체 건수 쿼리를 알아서 만든다.
     */
    @Query("""
            select w from Work w
            where w.type in :types
              and lower(w.title) like lower(concat('%', :q, '%'))
            """)
    Page<Work> search(@Param("types") Collection<WorkType> types,
                      @Param("q") String q,
                      Pageable pageable);
}
