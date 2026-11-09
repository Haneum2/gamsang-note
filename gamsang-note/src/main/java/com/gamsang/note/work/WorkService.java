package com.gamsang.note.work;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gamsang.note.genre.Genre;
import com.gamsang.note.work.WorkDtos.WorkDetail;
import com.gamsang.note.work.WorkDtos.WorkSummary;

/**
 * 서비스 계층: 비즈니스 로직과 트랜잭션 경계.
 * genres 는 LAZY 라서 DB 연결(영속성 컨텍스트)이 살아 있는 이 메서드 안에서 읽어야 한다.
 * (open-in-view 를 껐기 때문에, 컨트롤러에서 읽으면 LazyInitializationException 이 난다)
 */
@Service
@Transactional(readOnly = true)
public class WorkService {

    private final WorkRepository workRepository;
    private final MovieRepository movieRepository;
    private final BookRepository bookRepository;
    private final PerformanceRepository performanceRepository;

    public WorkService(WorkRepository workRepository, MovieRepository movieRepository,
                       BookRepository bookRepository, PerformanceRepository performanceRepository) {
        this.workRepository = workRepository;
        this.movieRepository = movieRepository;
        this.bookRepository = bookRepository;
        this.performanceRepository = performanceRepository;
    }

    public Page<WorkSummary> search(WorkType type, String q, Pageable pageable) {
        var types = (type == null) ? EnumSet.allOf(WorkType.class) : EnumSet.of(type);
        String keyword = (q == null) ? "" : q.trim();
        return workRepository.search(types, keyword, pageable).map(this::toSummary);
    }

    public WorkDetail get(Long id) {
        Work w = workRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "작품을 찾을 수 없습니다."));

        Integer runtime = null;
        String isbn = null;
        Integer pages = null;
        String venue = null;
        String period = null;

        switch (w.getType()) {
            case MOVIE -> runtime = movieRepository.findById(id).map(Movie::getRuntime).orElse(null);
            case BOOK -> {
                var b = bookRepository.findById(id).orElse(null);
                if (b != null) {
                    isbn = b.getIsbn();
                    pages = b.getPages();
                }
            }
            case PERFORMANCE -> {
                var p = performanceRepository.findById(id).orElse(null);
                if (p != null) {
                    venue = p.getVenue();
                    period = p.getPeriod();
                }
            }
        }

        return new WorkDetail(w.getId(), w.getType(), w.getTitle(), w.getReleaseDate(),
                w.getSummary(), w.getPosterUrl(), genreNames(w),
                runtime, isbn, pages, venue, period);
    }

    private WorkSummary toSummary(Work w) {
        return new WorkSummary(w.getId(), w.getType(), w.getTitle(), w.getReleaseDate(),
                w.getPosterUrl(), genreNames(w));
    }

    private List<String> genreNames(Work w) {
        return w.getGenres().stream().map(Genre::getName).sorted(Comparator.naturalOrder()).toList();
    }
}
