package com.gamsang.note.genre;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreRepository genreRepository;

    public GenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // GET /api/genres  →  장르 21건을 이름순으로 JSON 으로 돌려준다.
    @GetMapping
    public List<GenreResponse> list() {
        return genreRepository.findAll(Sort.by("name")).stream()
                .map(g -> new GenreResponse(g.getId(), g.getName()))
                .toList();
    }

    // 엔티티를 그대로 내보내지 않고 화면에 필요한 값만 담는 응답 객체
    public record GenreResponse(Long id, String name) {
    }
}
