package com.gamsang.note.work;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * work_person 연결 테이블. 기본키가 (work_id, person_id, role) 세 컬럼이라 복합키(@EmbeddedId)로 매핑한다.
 * 같은 사람이 한 작품에서 여러 역할(예: 감독 겸 배우)을 가질 수 있어서 role 까지 키에 들어 있다.
 */
@Entity
@Table(name = "work_person")
public class WorkPerson {

    // Hibernate 6.2+ 는 record 를 복합키(@Embeddable)로 쓸 수 있다. 값이 바뀌지 않는 키에 잘 맞는다.
    @Embeddable
    public record Key(@Column(name = "work_id") Long workId,
                      @Column(name = "person_id") Long personId,
                      @Column(name = "role", length = 30) String role) {
    }

    @EmbeddedId
    private Key id;

    protected WorkPerson() {
    }

    public WorkPerson(Long workId, Long personId, String role) {
        this.id = new Key(workId, personId, role);
    }

    public Key getId() { return id; }
}
