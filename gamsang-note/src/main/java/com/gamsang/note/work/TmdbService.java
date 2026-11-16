package com.gamsang.note.work;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TMDB(The Movie Database) 영화 API 연동. 응답은 JSON 이라 Spring 에 들어 있는 Jackson(JsonNode)으로 읽는다.
 *  - 검색: /search/movie (한국어 제목·줄거리: language=ko-KR)
 *  - 가져오기: /movie/{id}?append_to_response=credits → work + movie + work_genre + person/work_person(감독, 배우 5명)
 * 키는 환경 변수 TMDB_API_KEY(v3 키)로만 받는다.
 */
@Service
@Transactional(readOnly = true)
public class TmdbService {

    static final String SOURCE = "TMDB";
    private static final String API = "https://api.themoviedb.org/3";
    private static final String IMAGE = "https://image.tmdb.org/t/p/"; // + 크기(w342 등) + poster_path

    private final String key;
    private final RestClient http = RestClient.create();
    private final ExternalWorks externalWorks;
    private final WorkRepository workRepository;
    private final MovieRepository movieRepository;

    public TmdbService(@Value("${TMDB_API_KEY:}") String key, ExternalWorks externalWorks,
                       WorkRepository workRepository, MovieRepository movieRepository) {
        this.key = key;
        this.externalWorks = externalWorks;
        this.workRepository = workRepository;
        this.movieRepository = movieRepository;
    }

    public List<WorkDtos.ExternalItem> search(String q, int page) {
        if (q.isBlank()) return List.of(); // TMDB 검색은 검색어가 필수
        JsonNode results = get("/search/movie?query={q}&page={page}&include_adult=false", q, page + 1).path("results");
        List<JsonNode> movies = StreamSupport.stream(results.spliterator(), false).toList();

        Map<String, Long> imported = externalWorks.importedIds(SOURCE, movies.stream().map(m -> m.path("id").asText()).toList());
        return movies.stream().map(m -> {
            String id = m.path("id").asText();
            List<String> meta = new ArrayList<>();
            String date = text(m, "release_date");
            if (date != null) meta.add(date.substring(0, 4));
            String original = text(m, "original_title");
            if (original != null && !original.equals(text(m, "title"))) meta.add(original); // 원제가 다르면 같이 보여 준다
            return new WorkDtos.ExternalItem(id, text(m, "title"), meta, poster(m, "w342"), imported.get(id));
        }).toList();
    }

    /** 영화를 우리 DB에 저장하고 작품 번호를 돌려준다. 이미 있으면 기존 번호. */
    @Transactional
    public Long importMovie(String externalId) {
        var existing = externalWorks.existing(SOURCE, externalId);
        if (existing.isPresent()) return existing.get();

        JsonNode m = get("/movie/{id}?append_to_response=credits", externalId);
        Work work = new Work(WorkType.MOVIE, ExternalWorks.cut(text(m, "title"), 200), date(text(m, "release_date")),
                text(m, "overview"), poster(m, "w500"), SOURCE, externalId);
        m.path("genres").forEach(g -> externalWorks.addGenre(work, text(g, "name"))); // 이름이 같은 장르만(액션, 드라마, SF …)
        workRepository.save(work);

        int runtime = m.path("runtime").asInt(0); // movie.runtime 에 CHECK(runtime > 0) 가 있어서 0 이면 null
        movieRepository.save(new Movie(work.getId(), runtime > 0 ? runtime : null));

        List<String> directors = new ArrayList<>();
        m.path("credits").path("crew").forEach(c -> { if ("Director".equals(text(c, "job"))) directors.add(text(c, "name")); });
        List<String> cast = new ArrayList<>();
        m.path("credits").path("cast").forEach(c -> { if (cast.size() < 5) cast.add(text(c, "name")); });
        externalWorks.savePeople(work.getId(), directors, "감독"); // 역할 이름은 샘플 데이터와 같게
        externalWorks.savePeople(work.getId(), cast, "배우");
        return work.getId();
    }

    private JsonNode get(String path, Object... vars) {
        if (key.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "TMDB 키(TMDB_API_KEY)가 설정되지 않았습니다.");
        }
        Object[] all = new Object[vars.length + 1];
        System.arraycopy(vars, 0, all, 0, vars.length);
        all[vars.length] = key;
        try {
            return http.get().uri(API + path + "&language=ko-KR&api_key={key}", all).retrieve().body(JsonNode.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TMDB에 없는 영화입니다.");
        } catch (Exception e) {
            // 예외 메시지에 요청 주소(키 포함)가 들어갈 수 있어 원인을 그대로 넘기지 않는다.
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "TMDB에서 영화 정보를 가져오지 못했습니다.");
        }
    }

    private static String poster(JsonNode m, String size) {
        String path = text(m, "poster_path");
        return path == null ? null : IMAGE + size + path;
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.path(field);
        return v.isTextual() && !v.asText().isBlank() ? v.asText().trim() : null;
    }

    private static LocalDate date(String s) {
        try {
            return s == null ? null : LocalDate.parse(s);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
