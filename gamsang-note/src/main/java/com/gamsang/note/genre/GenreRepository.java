package com.gamsang.note.genre;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 인터페이스만 선언하면 Spring Data JPA 가 구현체를 만들어 준다.
 * findAll(), findById(), save(), delete() 등이 기본으로 들어 있다.
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
