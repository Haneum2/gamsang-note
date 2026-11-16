package com.gamsang.note.work;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.gamsang.note.genre.Genre;
import com.gamsang.note.genre.GenreRepository;

/**
 * KOPIS(공연예술통합전산망) 오픈API 연동. 응답은 XML 이라 JDK 기본 DOM 파서로 읽는다(의존성 추가 없음).
 *  - 검색: 목록 API(pblprfr). 기간(stdate~eddate)이 필수이고 31일을 넘을 수 없어서 "오늘 ±15일"에 걸친 공연을 찾는다.
 *  - 가져오기: 상세 API(pblprfr/{공연ID})로 work + performance + work_genre 행을 만든다.
 *    (source='KOPIS', external_id=공연ID) UNIQUE 라서 같은 공연은 한 번만 저장된다.
 * 서비스키는 환경 변수 KOPIS_KEY 로만 받는다.
 */
@Service
@Transactional(readOnly = true)
public class KopisService {

    static final String SOURCE = "KOPIS";
    // https 주소는 http 로 301 리다이렉트된다. 서버끼리 호출이라 http 를 바로 쓴다.
    private static final String BASE = "http://www.kopis.or.kr/openApi/restful/pblprfr";
    private static final DateTimeFormatter PARAM_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter KOPIS_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final String key;
    private final RestClient http = RestClient.create();
    private final WorkRepository workRepository;
    private final PerformanceRepository performanceRepository;
    private final GenreRepository genreRepository;

    public KopisService(@Value("${KOPIS_KEY:}") String key, WorkRepository workRepository,
                        PerformanceRepository performanceRepository, GenreRepository genreRepository) {
        this.key = key;
        this.workRepository = workRepository;
        this.performanceRepository = performanceRepository;
        this.genreRepository = genreRepository;
    }

    public List<WorkDtos.KopisItem> search(String q, int page) {
        LocalDate today = LocalDate.now(SEOUL);
        List<Element> rows = fetch(BASE + "?service={key}&stdate={from}&eddate={to}&cpage={page}&rows=20&shprfnm={q}",
                key, today.minusDays(15).format(PARAM_DATE), today.plusDays(15).format(PARAM_DATE), page + 1, q);

        // 이미 가져온 공연은 우리 작품 번호를 같이 돌려준다(화면에서 '가져오기' 대신 '열기').
        List<String> ids = rows.stream().map(e -> text(e, "mt20id")).toList();
        Map<String, Long> imported = workRepository.findBySourceAndExternalIdIn(SOURCE, ids).stream()
                .collect(Collectors.toMap(Work::getExternalId, Work::getId));

        return rows.stream().map(e -> new WorkDtos.KopisItem(
                text(e, "mt20id"), text(e, "prfnm"), period(e), text(e, "fcltynm"),
                text(e, "genrenm"), https(text(e, "poster")), imported.get(text(e, "mt20id")))).toList();
    }

    /** 공연을 우리 DB에 저장하고 작품 번호를 돌려준다. 이미 있으면 기존 번호. */
    @Transactional
    public Long importPerformance(String externalId) {
        Optional<Work> existing = workRepository.findBySourceAndExternalId(SOURCE, externalId);
        if (existing.isPresent()) return existing.get().getId();

        List<Element> rows = fetch(BASE + "/{id}?service={key}", externalId, key);
        // 없는 ID 면 KOPIS 는 빈 <db/> 를 돌려준다. 제목이 없으면 없는 공연으로 본다.
        if (rows.isEmpty() || text(rows.get(0), "prfnm") == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "KOPIS에 없는 공연입니다.");
        }
        Element e = rows.get(0);

        Work work = new Work(WorkType.PERFORMANCE, cut(text(e, "prfnm"), 200), date(text(e, "prfpdfrom")),
                blankToNull(text(e, "sty")), https(text(e, "poster")), SOURCE, externalId);
        genre(text(e, "genrenm")).ifPresent(work.getGenres()::add);
        workRepository.save(work); // IDENTITY 라서 save 시점에 INSERT 되고 번호가 정해진다
        performanceRepository.save(new Performance(work.getId(), cut(text(e, "fcltynm"), 100), period(e)));
        return work.getId();
    }

    // KOPIS 장르 이름 → 우리 genre 테이블. "서양음악(클래식)", "무용(서양/한국무용)"처럼 괄호가 붙어 오는 것만 바꿔 준다.
    // 대중음악·복합처럼 우리 장르에 없는 것은 장르 없이 저장한다.
    private Optional<Genre> genre(String kopisGenre) {
        if (kopisGenre == null) return Optional.empty();
        String name = kopisGenre.contains("클래식") ? "클래식" : kopisGenre.contains("무용") ? "무용" : kopisGenre;
        return genreRepository.findByName(name);
    }

    private List<Element> fetch(String uri, Object... vars) {
        if (key.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "KOPIS 서비스키(KOPIS_KEY)가 설정되지 않았습니다.");
        }
        try {
            // 바이트로 받는다: KOPIS 응답 헤더에 charset 이 없어 문자열로 받으면 ISO-8859-1 로 읽혀 한글이 깨진다.
            // XML 파서는 <?xml encoding="UTF-8"?> 선언을 보고 직접 디코딩한다.
            byte[] xml = http.get().uri(uri, vars).retrieve().body(byte[].class);
            if (xml == null || xml.length == 0) return List.of();
            var factory = DocumentBuilderFactory.newInstance();
            // 외부에서 받은 XML 이라 DOCTYPE(외부 엔티티, XXE)을 막는다.
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            var doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
            var nodes = doc.getDocumentElement().getElementsByTagName("db");
            List<Element> rows = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) rows.add((Element) nodes.item(i));
            return rows;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "KOPIS에서 공연 정보를 가져오지 못했습니다.", ex);
        }
    }

    private static String text(Element e, String tag) {
        Node n = e.getElementsByTagName(tag).item(0);
        return n == null ? null : blankToNull(n.getTextContent());
    }

    // 우리 샘플 데이터와 같은 형식: "2026-11-28 ~ 2026-11-29"
    private static String period(Element e) {
        String from = text(e, "prfpdfrom"), to = text(e, "prfpdto");
        if (from == null) return null;
        return to == null ? from.replace('.', '-') : (from + " ~ " + to).replace('.', '-');
    }

    private static LocalDate date(String s) {
        try {
            return s == null ? null : LocalDate.parse(s, KOPIS_DATE);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    // 포스터는 http 로 오지만 https 로도 열린다. 배포 사이트(https)에서 혼합 콘텐츠로 막히지 않게 바꾼다.
    private static String https(String url) {
        return url == null ? null : url.replaceFirst("^http://", "https://");
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static String cut(String s, int max) {
        return s == null || s.length() <= max ? s : s.substring(0, max);
    }
}
