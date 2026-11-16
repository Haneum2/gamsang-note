package com.gamsang.note.work;

import java.time.LocalDate;
import java.util.List;

/** 화면(React)으로 내보내는 응답 모양. 엔티티를 그대로 내보내지 않고 필요한 값만 담는다. */
public final class WorkDtos {

    private WorkDtos() {
    }

    /** 목록용 */
    public record WorkSummary(Long id, WorkType type, String title, LocalDate releaseDate,
                              String posterUrl, List<String> genres) {
    }

    /** 상세용: 공통 정보 + 종류별 정보(해당하지 않는 값은 null) */
    public record WorkDetail(Long id, WorkType type, String title, LocalDate releaseDate,
                             String summary, String posterUrl, List<String> genres,
                             Integer runtime,          // 영화
                             String isbn, Integer pages, // 책
                             String venue, String period, // 공연
                             String source,               // 외부 출처(TMDB/YES24/KOPIS), 샘플 작품은 null
                             String externalId,           // 출처 쪽 ID (YES24 상품 링크 등에 쓴다)
                             List<PersonItem> people) {   // 감독·배우·저자 등 (work_person)
    }

    /** 작품에 참여한 사람 한 명: 이름과 역할(감독, 배우, 저자, 출연 …) */
    public record PersonItem(String name, String role) {
    }

    /**
     * 외부 API(KOPIS, TMDB …) 검색 결과 한 건. meta 는 제목 아래 보여 줄 짧은 정보들(장르, 기간, 연도 …).
     * workId 는 이미 가져온 작품이면 우리 DB의 작품 번호, 아니면 null.
     */
    public record ExternalItem(String externalId, String title, List<String> meta, String posterUrl, Long workId) {
    }
}
