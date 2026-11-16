package com.gamsang.note.work;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gamsang.note.genre.GenreRepository;

/**
 * 외부 API(KOPIS, TMDB …)에서 작품을 가져올 때 공통으로 쓰는 DB 작업.
 * 호출하는 서비스의 @Transactional 안에서 실행된다.
 */
@Component
class ExternalWorks {

    private final WorkRepository workRepository;
    private final GenreRepository genreRepository;
    private final PersonRepository personRepository;
    private final WorkPersonRepository workPersonRepository;

    ExternalWorks(WorkRepository workRepository, GenreRepository genreRepository,
                  PersonRepository personRepository, WorkPersonRepository workPersonRepository) {
        this.workRepository = workRepository;
        this.genreRepository = genreRepository;
        this.personRepository = personRepository;
        this.workPersonRepository = workPersonRepository;
    }

    /** 이미 가져온 작품: 외부 ID → 우리 작품 번호 (검색 결과에 '열기'를 보여 주려고) */
    Map<String, Long> importedIds(String source, Collection<String> externalIds) {
        return workRepository.findBySourceAndExternalIdIn(source, externalIds).stream()
                .collect(Collectors.toMap(Work::getExternalId, Work::getId));
    }

    Optional<Long> existing(String source, String externalId) {
        return workRepository.findBySourceAndExternalId(source, externalId).map(Work::getId);
    }

    /** 장르 이름이 우리 genre 테이블에 있으면 붙인다(없으면 무시) */
    void addGenre(Work work, String name) {
        if (name != null) genreRepository.findByName(name).ifPresent(work.getGenres()::add);
    }

    /** person + work_person 저장.
     * ponytail: 같은 이름이어도 작품마다 새 person 행을 만든다(동명이인을 잘못 합치지 않으려고). 인물별 작품 모아 보기가 필요해지면 다시 본다. */
    void savePeople(Long workId, Collection<String> names, String role) {
        names.stream().map(String::trim).filter(n -> !n.isEmpty()).distinct().forEach(n -> {
            Person p = personRepository.save(new Person(cut(n, 100)));
            workPersonRepository.save(new WorkPerson(workId, p.getId(), role));
        });
    }

    static String cut(String s, int max) {
        return s == null || s.length() <= max ? s : s.substring(0, max);
    }
}
