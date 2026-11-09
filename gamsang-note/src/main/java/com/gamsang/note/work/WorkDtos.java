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
                             String venue, String period) { // 공연
    }
}
