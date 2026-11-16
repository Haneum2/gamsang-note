package com.gamsang.note.work;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkPersonRepository extends JpaRepository<WorkPerson, WorkPerson.Key> {

    /**
     * 작품에 참여한 사람(이름, 역할). 두 엔티티 사이에 연관관계 필드가 없어서 JPQL 의 "join ... on" 으로 직접 잇는다.
     * "select new 클래스(...)" 는 결과를 엔티티가 아니라 DTO 로 바로 만든다. 사람 번호순 = 시드·가져온 순서.
     */
    @Query("""
            select new com.gamsang.note.work.WorkDtos$PersonItem(p.name, wp.id.role)
            from WorkPerson wp join Person p on p.id = wp.id.personId
            where wp.id.workId = :workId
            order by p.id
            """)
    List<WorkDtos.PersonItem> findPeople(@Param("workId") Long workId);
}
