package com.gamsang.note.work;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.fasterxml.jackson.databind.node.MissingNode;

/**
 * YES24 Open API(도서) 연동. 키는 요청 헤더 X-Api-Key 로 보낸다(환경 변수 YES24_API_KEY).
 *  - 검색: /goods/itemList?category=BOOK
 *  - 가져오기: /goods/itemDetail?searchType=ItemId&detail=Y → work + book(ISBN, 쪽수) + work_genre + person(저자 등)
 * 약관: 화면에 YES24 출처와 상품 상세 페이지 링크를 보여야 한다(external_id = YES24 상품 번호로 링크를 만든다).
 *       사용자가 고른 책만 저장한다(대량·체계적 수집으로 별도 DB를 만드는 것은 금지).
 */
@Service
@Transactional(readOnly = true)
public class Yes24Service {

    static final String SOURCE = "YES24";
    private static final String API = "https://apis.yes24.com/v1";

    private final String key;
    private final RestClient http = RestClient.create();
    private final ExternalWorks externalWorks;
    private final WorkRepository workRepository;
    private final BookRepository bookRepository;

    public Yes24Service(@Value("${YES24_API_KEY:}") String key, ExternalWorks externalWorks,
                        WorkRepository workRepository, BookRepository bookRepository) {
        this.key = key;
        this.externalWorks = externalWorks;
        this.workRepository = workRepository;
        this.bookRepository = bookRepository;
    }

    public List<WorkDtos.ExternalItem> search(String q, int page) {
        if (q.isBlank()) return List.of();
        JsonNode data = get("/goods/itemList?query={q}&category=BOOK&page={page}&pageSize=20", q, page + 1);
        // 세트 상품은 ISBN 이 비어 있다. 책 한 권만 남긴다.
        List<JsonNode> books = StreamSupport.stream(data.path("items").spliterator(), false)
                .filter(i -> text(i, "isbn13") != null).toList();

        // 이미 있는 책: YES24 상품 번호로 가져온 것 + 같은 ISBN 이 우리 DB에 있는 것
        Map<String, Long> imported = new HashMap<>(externalWorks.importedIds(SOURCE, books.stream().map(i -> i.path("itemId").asText()).toList()));
        Map<String, Long> byIsbn = new HashMap<>();
        bookRepository.findByIsbnIn(books.stream().map(i -> text(i, "isbn13")).toList())
                .forEach(b -> byIsbn.put(b.getIsbn(), b.getWorkId()));

        return books.stream().map(i -> {
            String id = i.path("itemId").asText();
            List<String> meta = new ArrayList<>();
            LocalDate d = date(text(i, "publishDate"));
            if (d != null) meta.add(String.valueOf(d.getYear()));
            String authors = String.join(", ", people(text(i, "author")).getOrDefault("저자", List.of()));
            if (!authors.isEmpty()) meta.add(authors);
            if (text(i, "publisher") != null) meta.add(text(i, "publisher"));
            Long workId = imported.getOrDefault(id, byIsbn.get(text(i, "isbn13")));
            return new WorkDtos.ExternalItem(id, text(i, "title"), meta, text(i, "cover"), workId);
        }).toList();
    }

    /** 책을 우리 DB에 저장하고 작품 번호를 돌려준다. 이미 있으면(같은 상품 번호 또는 같은 ISBN) 기존 번호. */
    @Transactional
    public Long importBook(String itemId) {
        var existing = externalWorks.existing(SOURCE, itemId);
        if (existing.isPresent()) return existing.get();

        JsonNode items = get("/goods/itemDetail?searchType=ItemId&query={id}&detail=Y", itemId).path("items");
        if (items.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "YES24에 없는 상품입니다.");
        JsonNode b = items.get(0);
        String isbn = text(b, "isbn13");
        if (isbn != null) {
            var sameIsbn = bookRepository.findByIsbn(isbn); // book.isbn 은 UNIQUE 라 같은 책을 두 번 넣을 수 없다
            if (sameIsbn.isPresent()) return sameIsbn.get().getWorkId();
        }

        String intro = plain(b.path("contentDetail").isObject() ? text(b.path("contentDetail"), "bookIntroduction") : null);
        Work work = new Work(WorkType.BOOK, ExternalWorks.cut(text(b, "title"), 200), date(text(b, "publishDate")),
                ExternalWorks.cut(intro, 4000), text(b, "cover"), SOURCE, itemId);
        externalWorks.addGenre(work, genreName(text(b, "goodsSortNm")));
        workRepository.save(work);

        int pages = b.path("pages").asInt(0); // book.pages 에 CHECK(pages > 0)
        bookRepository.save(new Book(work.getId(), isbn, pages > 0 ? pages : null));
        people(text(b, "author")).forEach((role, names) -> externalWorks.savePeople(work.getId(), names, role));
        return work.getId();
    }

    // "한강 저", "A, B 저/C 역", "D 글/E 그림" → {저자:[한강], 옮긴이:[C], 그림:[E]} (역할 이름은 샘플 데이터와 같게 '저자')
    static Map<String, List<String>> people(String author) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        if (author == null) return out;
        for (String part : author.split("/")) {
            String p = part.trim();
            String role = p.endsWith(" 역") || p.endsWith(" 옮김") ? "옮긴이"
                    : p.endsWith(" 그림") ? "그림"
                    : p.endsWith(" 편") || p.endsWith(" 엮음") ? "엮은이" : "저자";
            String names = p.replaceFirst("\\s+(저|글|지음|글그림|글·그림|역|옮김|그림|편|엮음|원작|감수)$", "");
            Arrays.stream(names.split(",")).map(String::trim).filter(n -> !n.isEmpty()).limit(5)
                    .forEach(n -> out.computeIfAbsent(role, r -> new ArrayList<>()).add(n));
        }
        return out;
    }

    // YES24 분류("국내도서-소설/시/희곡") → 우리 장르 이름. 없는 분류는 장르 없이 저장된다.
    private static String genreName(String sort) {
        if (sort == null) return null;
        for (String g : List.of("추리", "판타지", "SF", "소설", "에세이", "인문", "역사", "과학", "자기계발")) {
            if (sort.contains(g)) return g;
        }
        return null;
    }

    private JsonNode get(String path, Object... vars) {
        if (key.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "YES24 키(YES24_API_KEY)가 설정되지 않았습니다.");
        }
        try {
            JsonNode body = http.get().uri(API + path, vars).header("X-Api-Key", key).retrieve().body(JsonNode.class);
            return body == null ? MissingNode.getInstance() : body.path("data");
        } catch (HttpClientErrorException.NotFound e) {
            return MissingNode.getInstance(); // SEARCH_001(결과 없음), GOODS_001 등
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "YES24에서 도서 정보를 가져오지 못했습니다.");
        }
    }

    // 책 소개에는 <b>, <br> 같은 HTML 이 섞여 온다. 화면은 글자만 보여 주므로 태그를 지우고 빈 줄을 하나로 줄인다.
    // ponytail: &amp; 같은 HTML 엔티티는 흔한 몇 개만 바꾼다. 다른 엔티티가 보이면 여기에 더한다.
    static String plain(String html) {
        if (html == null) return null;
        String t = html.replaceAll("(?i)<br\\s*/?>", "\n").replaceAll("<[^>]+>", "")
                .replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
        t = String.join("\n", t.lines().map(String::strip).filter(l -> !l.isEmpty()).toList());
        return t.isEmpty() ? null : t;
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.path(field);
        return v.isTextual() && !v.asText().isBlank() ? v.asText().trim() : null;
    }

    // 문서에는 2013-12-24 형식이지만 실제 응답은 20140519 형식이라 둘 다 받는다.
    private static LocalDate date(String s) {
        if (s == null) return null;
        try {
            return s.length() == 8 ? LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE) : LocalDate.parse(s.substring(0, 10));
        } catch (RuntimeException e) {
            return null;
        }
    }
}
