# 감상노트 (작품 기록 & 후기 공유 앱) — Claude Code 작업 안내

이 파일은 이 폴더에서 Claude Code를 실행할 때 가장 먼저 읽는 프로젝트 안내서다. 이전 대화(claude.ai)에서 만든 내용을 이어받기 위한 것이다.

## 1. 프로젝트 개요
- 대학 **데이터베이스 응용** 수업(담당 교수 정민아) 팀 프로젝트. 팀 4명(조한흠, 장재영, 문지현, 김동주). 사용자는 팀원 중 한 명이고 이 폴더의 개발을 맡았다.
- 영화·책·공연을 기록(보고 싶어요/보는 중/봤어요, 별점, 감상일)하고, 후기를 공개 범위(전체/친구/나만 보기)로 공유하고, 친구·좋아요·댓글·통계·연말 결산을 제공하는 앱.
- **마감 2026-11-19, 내부 목표는 11-18.** 앱 개발 + DB 구축 + 보고서(13개 장)가 모두 끝나 있어야 한다.
- 스택: **Spring Boot 3.3.5 + JPA(Hibernate 6) + Oracle XE 21c + React 18(Vite)**. Java 17, Maven, Node 22.
- 보고서 초안은 claude.ai의 Claude Docs 문서 "감상노트 프로젝트 보고서 (초안)"에 있다(https://claude.ai/artifact/XUJUDzVMCwh3qudfUNpSVF). 이 폴더에서는 읽을 수 없으니, 보고서에 영향을 주는 변경(테이블·컬럼·API 변경)은 사용자에게 알려 반영하게 한다.

## 2. 작업 원칙 (중요)
- **한국어로 답한다.** 사용자는 앱·DB 구축 과정을 **이해하면서** 만들고 싶어 한다. 코드를 바꿀 때 무엇을 왜 그렇게 했는지(JPA·Spring·React 개념) 짧게 설명한다. 과하게 길게 쓰지는 않는다.
- 작게 바꾸고, **직접 실행해서 확인**한다(`mvn` 컴파일, `npm run dev`, API 호출). 이전에는 코드를 컴파일·실행해 보지 못한 채 작성했으니, 컴파일 오류나 어긋남이 있으면 먼저 고친다.
- **비밀번호·접속 정보를 파일에 쓰거나 커밋하지 않는다.** DB 비밀번호는 환경 변수 `DB_PASSWORD`로만 읽는다. 사용자에게 비밀번호를 묻지 않는다.
- DB 스키마를 바꾸면 `db/` 스크립트(01_schema, 02_seed, 03_views_triggers, 00_drop)도 함께 고치고 사용자에게 알린다(보고서 8·11장에 반영 필요). 데이터를 지우거나 테이블을 다시 만드는 작업은 하기 전에 확인한다.
- 앱 코드를 바꿀 때는 이미 검증된 동작(아래 6번)을 깨지 않는다.

## 3. 폴더 구조
```
(이 폴더)
├─ CLAUDE.md
├─ db/                  Oracle 스크립트 (00_drop, 01_schema, 02_seed, 03_views_triggers, 04_queries, 05_constraint_tests, README)
├─ gamsang-note/        백엔드 (Spring Boot, Maven)
│   └─ src/main/java/com/gamsang/note/{genre,work,user,viewing,social,feed,stats}
└─ gamsang-note-web/    프론트엔드 (React + Vite)
    └─ src/{api.js, UserContext.jsx, labels.js, pages/, components/}
```

## 4. 실행 방법 (Windows)
1. Oracle XE(서비스 `OracleServiceXE`, 리스너)가 실행 중이어야 한다. 접속: `localhost:1521`, 서비스명 **XEPDB1**, 계정 **SYSTEM**.
2. 터미널에 `set DB_PASSWORD=...` (PowerShell은 `$env:DB_PASSWORD="..."`) 후 백엔드 실행: `cd gamsang-note` → `mvn spring-boot:run` (또는 IntelliJ에서 `NoteApplication` 실행, 환경 변수 `DB_PASSWORD` 설정). 포트 8080.
3. 프론트: `cd gamsang-note-web` → `npm install`(처음 한 번) → `npm run dev`. 포트 5173. `/api`는 vite 프록시로 8080에 전달되므로 CORS 설정이 필요 없다.
4. DB를 처음부터 만들 때: SQL Developer로 `db/01_schema.sql` → `02_seed.sql` → `03_views_triggers.sql` 순서로 실행. 재실행은 `00_drop.sql` 먼저. 검증 건수: 회원 12, 작품 105, 감상 기록 244, 후기 107, 좋아요 154, 댓글 85, 친구 19, 컬렉션 9.

## 5. DB 요약 (Oracle, 16개 테이블)
- 회원 `users`(user_id, nickname, email, created_at) — **로그인/비밀번호 컬럼이 없다**(인증 방식 미정).
- 작품: 슈퍼타입 `work`(type: MOVIE/BOOK/PERFORMANCE) → 서브타입 `movie`(runtime), `book`(isbn,pages), `performance`(venue,period). PK=FK(work_id). 장르 `genre`, 연결 `work_genre`. 인물 `person`, 연결 `work_person`(role 포함).
- `record`(감상 기록; user당 작품 1건, UNIQUE(user_id, work_id); status WANT/WATCHING/DONE; rating 0.5~5.0, 0.5 단위 CHECK; watched_date).
- `review`(record_id, content CLOB, spoiler_yn Y/N, visibility ALL/FRIEND/PRIVATE, created_at), `review_like`(review_id,user_id), `comments`(review 댓글).
- `friendship`(requester_id, receiver_id, status PENDING/ACCEPTED, 자기 자신 금지 CHECK, 함수 기반 유일 인덱스 `uq_friendship_pair`로 **반대 방향 중복 요청 차단**).
- `collection`, `collection_item` (컬렉션; 아직 앱 코드 없음).
- 뷰: `v_visible_review`(viewer_id별로 볼 수 있는 후기; 공개 범위 규칙의 단일 출처), `v_friend_feed`(친구의 전체/친구 공개 후기).
- 트리거: `trg_review_mark_done` — 후기를 저장하면 해당 record.status를 `DONE`으로 바꾼다(앱 코드는 상태를 바꾸지 않는다).
- 테이블 이름 `users`, `comments`는 Oracle 예약어(USER, COMMENT) 때문이다. 현재 테이블이 **SYSTEM 스키마**에 있다(시연용으론 가능, 나중에 전용 계정으로 옮기는 것이 안전).
- `work.type`과 서브타입 행이 맞는지는 DB가 막지 않는다(앱에서 지킨다).

## 6. 백엔드 구조와 규칙
- 패키지 기준 구조: `controller → service → repository(Spring Data JPA) → entity`. 요청·응답은 `record` 타입 DTO(`*Dtos.java`), 엔티티를 그대로 내보내지 않는다.
- `application.yml`: `ddl-auto: none`(테이블은 SQL 스크립트가 관리), `open-in-view: false`(지연 로딩은 `@Transactional` 서비스 안에서만), `show-sql: true`, 비밀번호는 `${DB_PASSWORD}`. 오류 메시지를 화면에 보이려면 `server.error.include-message: always`가 필요하다(**적용 여부 확인**).
- 오류는 `ResponseStatusException`(400/403/404/409). 입력 검증은 서비스에서 직접 한다(`@Valid` 미도입).
- 뷰(`v_visible_review`, `v_friend_feed`)는 `@Immutable` 읽기 전용 엔티티로 매핑(`@IdClass(VisibleReviewId)`).
- **로그인이 없어서 `userId`를 요청 파라미터/본문으로 받는 임시 방식**이다. 인증을 도입하면 한꺼번에 교체해야 한다.
- 이미 쓴 JPA 개념(설명에 활용): `@Entity/@Id/IDENTITY`, `@ManyToOne(LAZY)`, `@ManyToMany`+`@JoinTable`+`@BatchSize`, `@EmbeddedId` 복합키, `@Convert(YesNoConverter)`, `@CreationTimestamp`, `@EntityGraph`, `@Query`+`@Param`(JPQL, `nulls last`, countQuery), JPQL 집계(`Object[]`), `Pageable`, 변경 감지(`save()` 없이 UPDATE).

## 7. API 목록 (백엔드 검증 완료)
| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| GET | /api/genres | 장르 21건 |
| GET | /api/works?type=&q=&page=&size= | 작품 검색(제목순, 페이징) |
| GET | /api/works/{id} | 작품 상세(종류별 필드 포함) |
| GET | /api/users | 회원 목록(임시 '나' 선택용) |
| POST | /api/records | 감상 기록 등록 (이미 있으면 409) |
| PATCH | /api/records/{id} | 상태·별점·감상일 수정(null은 변경 안 함) |
| GET | /api/records?userId=&status=&page=&size= | 내 기록 목록 |
| POST | /api/records/{id}/review | 후기 작성(공개 범위, 스포일러) |
| GET | /api/reviews?viewerId= | 볼 수 있는 후기(공개 범위 적용) |
| GET | /api/feed?viewerId= | 친구 피드 |
| POST/DELETE | /api/reviews/{id}/likes | 좋아요 / 취소 |
| GET/POST | /api/reviews/{id}/comments | 댓글 |
| GET | /api/reviews/{id}/social?viewerId= | 좋아요 수·댓글 수·내가 눌렀는지 |
| POST | /api/friends/requests | 친구 요청 |
| POST | /api/friends/requests/accept | 수락 |
| DELETE | /api/friends/requests?requesterId=&receiverId= | 거절/취소(대기 중만) |
| GET | /api/friends/requests?userId= | 내가 받은 대기 요청 |
| GET | /api/friends?userId= | 내 친구 목록 |
| GET | /api/stats/monthly?userId=&year= | 월별 감상 수(12개월) |
| GET | /api/stats/genres?userId= | 장르별 수·비율·평균 별점 |
| GET | /api/stats/year-end?userId=&year= | 연말 결산 |

사용자가 직접 확인한 결과(샘플 데이터, 1번 회원): 친구 2·3·5·11번, 후기 67건, 2026년 월별 3월 3/6월 2/8월 1/9월 1, 연말 결산 총 7건(영화 4·책 2·공연 1), 선호 장르 SF 3·판타지 2.

## 8. 프론트엔드 구조
- `api.js`: 서버 호출을 한곳에 모은 모듈(`ApiError` 포함). 화면은 `fetch`를 직접 쓰지 않는다.
- `UserContext.jsx`: 임시 '나' 선택(localStorage `gamsang.userId`). 인증 도입 시 교체 지점.
- 페이지: `WorksPage`, `RecordsPage`, `ReviewsPage`(후기·친구 피드), `FriendsPage`(받은 요청 수락·거절, 친구 목록, 요청 보내기), `StatsPage`(연도 탭, 연말 결산, 월별 막대, 장르 표). 컴포넌트: `WorkDetail`(작품 상세+기록 등록), `ReviewForm`, `ReviewCard`(좋아요·댓글), `Pager`, `Cover`(표지), `Stars`(숫자+5칸 막대), `Tabs`(글자 탭). `RecordRow`는 `RecordsPage.jsx` 안에 있다.
- 회원을 바꾸면 화면 상태를 초기화하려고 `<Route element={<... key={userId} />}>`를 쓴다.
- **디자인 규칙(2026-11-09, 새 화면도 따른다)**: 토큰과 공용 클래스는 `styles.css` 맨 위에 있다.
  - 색: 배경 #ECEEF1, 면 #FFF, 글자 #1B1F2A, 보조 #5E6573, 선 #CFD3DA. 작품 종류 색(영화 #2A3B8F, 책 #17695A, 공연 #A3234F) 외에 강조색은 두지 않는다. 버튼·포커스는 먹색.
  - 글꼴: 본문·로고·제목은 Pretendard(제목 800). Black Han Sans는 **표지 제목에만** 쓴다. 작은 크기에서는 획이 뭉쳐 읽기 어렵다는 사용자 피드백이 있었다.
  - 글자 크기는 13/15/18/28px, 모서리는 표지 2px·입력 6px·패널 10px.
  - 쓰지 않는 것: 이모지, 알약 칩, 카드 그림자·호버 떠오름, 가운데 점(·)으로 이어 붙인 정보, eyebrow 라벨. 움직임은 상세 패널이 160ms 미끄러지는 것 하나뿐이다.
  - 공용 클래스: 탭 `Tabs`, 목록 `.tickets`/`.entries`, 버튼 `.btn`(`.primary`, `.text`), 알림 `.notice`(`.error`/`.ok`), 빈 화면 `.empty`.
  - 빈 화면 문구는 다음 행동을 안내하고, 오류 문구는 "무엇을 못 했는지 + 서버 메시지"로 쓴다.
  - 360px에서 가로 넘침이 없어야 한다.

## 9. 현재 상태
- 백엔드 모든 API: 사용자가 브라우저에서 확인 완료(위 7번).
- 프론트: 작품 목록 화면 확인 완료. RecordsPage·ReviewsPage·ReviewForm·ReviewCard·Pager는 2026-11-09에 다시 작성했다(이전 파일이 폴더에 없었음). 같은 날 API와 브라우저로 확인 완료(기록 수정, 후기 작성→트리거로 DONE, 스포일러 가리기, 볼 수 있는 후기/친구 피드, 좋아요·취소, 댓글). FriendsPage·StatsPage도 같은 날 확인(요청 보내기·중복 409·거절, 통계 수치가 7번 확인값과 일치). 친구 '수락'은 화면에서 누르지 않았다(친구 끊기 API가 없어 데이터가 남음).
- 내가 **보낸** 대기 요청 목록 API가 없어서, 친구 화면에서 보낸 요청을 보거나 취소할 수 없다. 요청 후보 목록에도 이미 요청을 보낸 회원이 남는다(다시 보내면 409 안내).
- 종류 색 중 책(#17695A)과 공연(#A3234F)은 적록색약에서 거의 구분되지 않는다(dataviz 검사 ΔE 4.0). 그래서 종류는 항상 글자('영화/책/공연')와 함께 표시한다.
- DB에 이전 테스트 데이터가 남아 있다(사용자가 그대로 두기로 함): 기록 245('지젤')·후기 108·좋아요 2건, 친구 요청 1→12 PENDING. 그래서 검증 건수와 다르다.
- Git Bash에서 curl로 한글 JSON을 보내면 인코딩이 깨져 400이 난다. 본문은 UTF-8 파일로 만들어 `--data-binary @파일`로 보낸다.
- Maven은 PATH에 없다. IntelliJ에 포함된 것을 쓴다: `"C:\Program Files\JetBrains\IntelliJ IDEA 2026.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"`.

## 10. 남은 작업 (우선순위 순)
1. ~~프론트 2단계 실행 확인과 오류 수정~~ (완료).
2. ~~`application.yml`에 `server.error.include-message: always` 적용 확인~~ (적용되어 있음).
3. ~~내 기록 정렬~~ (완료). `Sort.nullsLast()`는 메서드 이름 쿼리에서 SQL에 반영되지 않아서, `ViewingRecordRepository.findMine`의 `@Query`(JPQL `order by ... nulls last`)로 바꿨다. status는 `(:status is null or ...)`로 선택 조건.
4. ~~프론트 친구 화면과 통계·연말 결산 화면~~ (완료, 2026-11-09). 남은 것: 보낸 요청 목록·취소 API(`GET /api/friends/requests/sent`)가 생기면 화면에 추가.
5. 작품 상세에 **인물(감독·배우)** 표시(work_person → API 추가).
6. **로그인/인증 방식 결정**(자체 가입이면 `users`에 비밀번호 해시 컬럼 추가, 또는 임시 선택 방식 유지) — 팀 결정 필요, 보고서 영향 있음.
7. 부가 기능: 컬렉션 API·화면, **같이 볼 작품 추천**(쿼리는 `db/04_queries.sql`의 Q12·Q13).
8. 품질: 목록 후기에 좋아요·댓글 수를 함께 내려 N+1 호출 줄이기, `@Valid` 입력 검증, 기록 별점 지우기(PATCH가 null을 무시함), 에러 응답 형식 통일, 간단한 테스트.
9. 발표·보고서용: 화면 캡처, `db/05_constraint_tests.sql`·`04_queries.sql` 실행 결과 캡처, README(실행 방법), 시연 시나리오.

## 11. 알려진 주의점
- 샘플 데이터(작품·회원·후기)는 시연용 임의 값이다. 작품 정보는 TMDB·도서 API와 대조가 필요하다.
- Oracle은 내림차순 정렬에서 `NULL`을 앞에 둔다(위 10-3).
- 후기를 쓰면 DB 트리거가 기록 상태를 바꾸므로, 같은 트랜잭션 안에서 이미 읽어 둔 `record` 엔티티는 오래된 값일 수 있다(별도 요청으로 다시 읽으면 최신).
