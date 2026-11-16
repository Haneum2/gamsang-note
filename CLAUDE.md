# 감상노트 (작품 기록 & 후기 공유 앱) — Claude Code 작업 안내

이 파일은 이 폴더에서 Claude Code를 실행할 때 가장 먼저 읽는 프로젝트 안내서다. 이전 대화(claude.ai)에서 만든 내용을 이어받기 위한 것이다.

## 1. 프로젝트 개요
- 대학 **데이터베이스 응용** 수업 팀 프로젝트. 팀 4명. 사용자는 팀원 중 한 명이고 이 폴더의 개발을 맡았다.
- 영화·책·공연을 기록(보고 싶어요/보는 중/봤어요, 별점, 감상일)하고, 후기를 공개 범위(전체/친구/나만 보기)로 공유하고, 친구·좋아요·댓글·통계·연말 결산을 제공하는 앱.
- **마감 2026-11-19, 내부 목표는 11-18.** 앱 개발 + DB 구축 + 보고서(13개 장)가 모두 끝나 있어야 한다.
- 스택: **Spring Boot 3.3.5 + JPA(Hibernate 6) + Oracle XE 21c + React 18(Vite)**. Java 17, Maven, Node 22.
- **팀 결정(2026-11-10 회의)**: ① 필수 기능까지만 구현하고 시간이 남으면 부가 기능 ② 회원은 **자체 가입**(비밀번호 해시 저장) ③ 작품 데이터는 **외부 API**(영화 TMDB, 책 YES24(2026-11-16 변경: 알라딘 키 발급 종료), 공연 KOPIS)에서 가져온다 ④ 배포는 **Render**, 클라우드 DB는 **Turso**.
- **DB는 2벌**: 기본 프로필=Oracle(로컬 개발·보고서 기준), `prod` 프로필=Turso(libSQL/SQLite, 배포용 이식본). 스키마를 바꾸면 `db/`(Oracle)와 `db/turso/`를 **항상 같이** 고친다. 엔티티에서 `@Lob`은 쓰지 않는다(SQLite 호환). 배포 절차는 `docs/DEPLOY.md`.
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
│   └─ turso/           Turso(SQLite) 이식본 (01_schema, 02_seed, 03_views_triggers, gamsang.db, gamsang_dump.sql)
├─ docs/DEPLOY.md       Turso·Render 배포 가이드
├─ Dockerfile, render.yaml   Render 배포(서비스 1개, 프론트는 Spring Boot가 함께 서빙: SpaController)
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
5. prod 프로필(Turso) 로컬 시험·배포: `docs/DEPLOY.md` 참고. 환경 변수 `SPRING_PROFILES_ACTIVE=prod`, `TURSO_JDBC_URL`, `TURSO_AUTH_TOKEN`(`application-prod.yml`). 접속 주소·토큰은 파일·채팅·커밋에 쓰지 않는다.

## 5. DB 요약 (Oracle, 16개 테이블)
- 회원 `users`(user_id, nickname, email, created_at, password_hash) — password_hash 는 BCrypt(2026-11-16 추가, 기존 DB는 `db/07_add_password_hash.sql`·`db/turso/07_add_password_hash.sql`). 샘플 회원 12명 비밀번호는 모두 데모용 `gamsang1234`.
- 작품: 슈퍼타입 `work`(type: MOVIE/BOOK/PERFORMANCE, 외부 출처 `source` TMDB/YES24/KOPIS + `external_id`, UNIQUE(source, external_id), 둘 다 NULL이거나 둘 다 값 CHECK; 2026-11-16 추가, 기존 DB는 `db/06_add_work_source.sql`·`db/turso/06_add_work_source.sql`) → 서브타입 `movie`(runtime), `book`(isbn,pages), `performance`(venue,period). PK=FK(work_id). 장르 `genre`, 연결 `work_genre`. 인물 `person`, 연결 `work_person`(role 포함).
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
- **로그인(2026-11-16)**: `/api/auth/signup·login·logout·me`(`AuthController`), 서버 세션(JSESSIONID, HttpOnly·SameSite=Lax, prod는 Secure). 비밀번호는 `spring-security-crypto`의 BCrypt만 쓴다(Spring Security 전체는 미도입). 컨트롤러는 `@LoginUser Long me`(세션의 회원 번호, 없으면 401, `LoginUserResolver`)로 받고, 요청에 userId 를 받지 않는다. 남의 기록 수정·후기는 403. 로그인 시도 횟수 제한은 없다.
- 이미 쓴 JPA 개념(설명에 활용): `@Entity/@Id/IDENTITY`, `@ManyToOne(LAZY)`, `@ManyToMany`+`@JoinTable`+`@BatchSize`, `@EmbeddedId` 복합키, `@Convert(YesNoConverter)`, `@CreationTimestamp`, `@EntityGraph`, `@Query`+`@Param`(JPQL, `nulls last`, countQuery), JPQL 집계(`Object[]`), `Pageable`, 변경 감지(`save()` 없이 UPDATE).

## 7. API 목록 (백엔드 검증 완료)
| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| GET | /api/genres | 장르 21건 |
| GET | /api/works?type=&q=&page=&size= | 작품 검색(제목순, 페이징) |
| GET | /api/works/{id} | 작품 상세(종류별 필드 + source + people[이름·역할] 포함) |
| GET | /api/works/kopis?q=&page= | KOPIS 공연 검색(오늘 ±15일, 20건, 가져온 공연은 workId 포함) |
| POST | /api/works/kopis/{mt20id} | KOPIS 공연을 work+performance+work_genre로 저장(이미 있으면 기존 것), 작품 상세 반환 |
| POST | /api/auth/signup · login · logout | 가입(바로 로그인)·로그인·로그아웃 |
| GET | /api/auth/me | 로그인한 회원(아니면 401) |
| GET | /api/users | 회원 목록(친구 요청 상대 고르기, 로그인 필요) |
| POST | /api/records | 감상 기록 등록 (이미 있으면 409) |
| PATCH | /api/records/{id} | 상태·별점·감상일 수정(null은 변경 안 함) |
| GET | /api/records?status=&page=&size= | 내 기록 목록 |
| POST | /api/records/{id}/review | 후기 작성(공개 범위, 스포일러) |
| GET | /api/reviews | 볼 수 있는 후기(공개 범위 적용) |
| GET | /api/feed | 친구 피드 |
| POST/DELETE | /api/reviews/{id}/likes | 좋아요 / 취소 |
| GET/POST | /api/reviews/{id}/comments | 댓글 |
| GET | /api/reviews/{id}/social | 좋아요 수·댓글 수·내가 눌렀는지 |
| POST | /api/friends/requests | 친구 요청 |
| POST | /api/friends/requests/accept | 수락 |
| DELETE | /api/friends/requests?requesterId=&receiverId= | 거절/취소(대기 중만) |
| GET | /api/friends/requests | 내가 받은 대기 요청 |
| GET | /api/friends/requests/sent | 내가 보낸 대기 요청(취소는 DELETE ?requesterId=나&receiverId=상대) |
| GET | /api/friends | 내 친구 목록 |
| GET | /api/stats/monthly?year= | 월별 감상 수(12개월) |
| GET | /api/stats/genres | 장르별 수·비율·평균 별점 |
| GET | /api/stats/year-end?year= | 연말 결산 |

작품 목록·상세·장르·KOPIS 검색 외에는 모두 로그인이 필요하다(회원 번호는 세션에서). 친구 요청 본문은 `{receiverId}`, 수락은 `{requesterId}`.

사용자가 직접 확인한 결과(샘플 데이터, 1번 회원): 친구 2·3·5·11번, 보이는 후기 66건(원본 샘플 기준. 테스트 후기 108 포함 시 67, 148까지 68), 2026년 월별 3월 3/6월 2/8월 1/9월 1, 연말 결산 총 7건(영화 4·책 2·공연 1), 선호 장르 SF 3·판타지 2.

## 8. 프론트엔드 구조
- `api.js`: 서버 호출을 한곳에 모은 모듈(`ApiError` 포함). 화면은 `fetch`를 직접 쓰지 않는다.
- `UserContext.jsx`: 로그인 상태(`me`: undefined 확인 중/null 로그아웃/객체). 시작할 때 `/api/auth/me`, 다른 요청이 401이면 `api.js`가 `gamsang:unauthorized` 이벤트를 보내 로그인 화면으로. `AuthPage`(로그인·가입 탭).
- 페이지: `WorksPage`, `RecordsPage`, `ReviewsPage`(후기·친구 피드), `FriendsPage`(받은 요청 수락·거절, 친구 목록, 요청 보내기), `StatsPage`(연도 탭, 연말 결산, 월별 막대, 장르 표). 컴포넌트: `WorkDetail`(작품 상세+기록 등록), `ReviewForm`, `ReviewCard`(좋아요·댓글), `Pager`, `Cover`(표지), `Stars`(숫자+5칸 막대), `Tabs`(글자 탭). `RecordRow`는 `RecordsPage.jsx` 안에 있다.
- 다른 계정으로 로그인하면 화면 상태를 초기화하려고 `<Route element={<... key={userId} />}>`를 쓴다.
- **디자인 규칙(2026-11-09, 새 화면도 따른다)**: 토큰과 공용 클래스는 `styles.css` 맨 위에 있다.
  - 색: 배경 #ECEEF1, 면 #FFF, 글자 #1B1F2A, 보조 #5E6573, 선 #CFD3DA. 작품 종류 색(영화 #2A3B8F, 책 #17695A, 공연 #A3234F) 외에 강조색은 두지 않는다. 버튼·포커스는 먹색.
  - 표지: `posterUrl`이 있으면 포스터 이미지(같은 2:3 틀, object-fit: cover, 불러오기 실패 시 글자 표지), 없으면 종류 색 글자 표지(2026-11-16).
  - 글꼴: 본문·로고·제목은 Pretendard(제목 800). Black Han Sans는 **표지 제목에만** 쓴다. 작은 크기에서는 획이 뭉쳐 읽기 어렵다는 사용자 피드백이 있었다.
  - 글자 크기는 13/15/18/28px, 모서리는 표지 2px·입력 6px·패널 10px.
  - 쓰지 않는 것: 이모지, 알약 칩, 카드 그림자·호버 떠오름, 가운데 점(·)으로 이어 붙인 정보, eyebrow 라벨. 움직임은 상세 패널이 160ms 미끄러지는 것 하나뿐이다.
  - 공용 클래스: 탭 `Tabs`, 목록 `.tickets`/`.entries`, 버튼 `.btn`(`.primary`, `.text`), 알림 `.notice`(`.error`/`.ok`), 빈 화면 `.empty`.
  - 빈 화면 문구는 다음 행동을 안내하고, 오류 문구는 "무엇을 못 했는지 + 서버 메시지"로 쓴다.
  - 360px에서 가로 넘침이 없어야 한다.

## 9. 현재 상태
- 백엔드 모든 API: 사용자가 브라우저에서 확인 완료(위 7번).
- 프론트: 작품 목록 화면 확인 완료. RecordsPage·ReviewsPage·ReviewForm·ReviewCard·Pager는 2026-11-09에 다시 작성했다(이전 파일이 폴더에 없었음). 같은 날 API와 브라우저로 확인 완료(기록 수정, 후기 작성→트리거로 DONE, 스포일러 가리기, 볼 수 있는 후기/친구 피드, 좋아요·취소, 댓글). FriendsPage·StatsPage도 같은 날 확인(요청 보내기·중복 409·거절, 통계 수치가 7번 확인값과 일치). 친구 '수락'은 화면에서 누르지 않았다(친구 끊기 API가 없어 데이터가 남음).
- 친구 화면: 받은 요청(수락·거절), 보낸 요청(취소), 친구 목록, 요청 보내기(이미 친구·요청 중인 회원은 후보에서 뺀다). 2026-11-16.
- 종류 색 중 책(#17695A)과 공연(#A3234F)은 적록색약에서 거의 구분되지 않는다(dataviz 검사 ΔE 4.0). 그래서 종류는 항상 글자('영화/책/공연')와 함께 표시한다.
- DB에 이전 테스트 데이터가 남아 있다(사용자가 그대로 두기로 함): 기록 245('지젤')·후기 108·좋아요 2건, 친구 요청 1→12 PENDING. 그래서 검증 건수와 다르다.
- Git Bash에서 curl로 한글 JSON을 보내면 인코딩이 깨져 400이 난다. 본문은 UTF-8 파일로 만들어 `--data-binary @파일`로 보낸다.
- Maven은 PATH에 없다. IntelliJ에 포함된 것을 쓴다: `"C:\Program Files\JetBrains\IntelliJ IDEA 2026.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"`.

## 10. 남은 작업 (우선순위 순, 2026-11-10 회의 반영)
완료: 프론트 2단계, `include-message` 적용, 내 기록 정렬(`findMine` JPQL `nulls last`), 친구·통계 화면(2026-11-09).
**필수**
1. ~~Oracle 회귀(`@Lob` 제거)~~ (완료 2026-11-11: 13KB 후기·2KB 댓글 저장·조회 정상).
2. ~~Turso 접속 시험~~ (완료 2026-11-16, 로컬 prod. 드라이버 결함 4가지를 `TursoConfig`·`TursoDialect`·gson 버전으로 우회, prod는 트랜잭션 없이 자동 커밋. `docs/DEPLOY.md` 5-1). Turso 접속 정보는 사용자 환경 변수 `TURSO_JDBC_URL`·`TURSO_AUTH_TOKEN`.
3. Render 배포(`render.yaml`), 무료 플랜 절전 대비 절차를 README에.
4. ~~자체 가입/로그인~~ (완료 2026-11-16, Oracle·Turso(prod) 모두 API 24개 항목 통과, 브라우저 확인. Oracle·Turso에 07 적용 완료. prod 쿠키는 Secure라 로컬 http 시험 때는 SERVER_SERVLET_SESSION_COOKIE_SECURE=false).
5. 외부 작품 API 연동: 영화 TMDB, 도서 YES24(공개 API 존재·키 확인 필요), 공연 KOPIS(**완료 2026-11-16**: `KopisService`, 작품 화면 공연 탭의 'KOPIS에서 찾기', 상세에 출처 표시. 키 `KOPIS_KEY`는 사용자 환경 변수·Render 환경 변수. 출연진→person은 6번에서).
   - KOPIS: 응답 XML. 목록 `/openApi/restful/pblprfr?service=&stdate=&eddate=(31일 이내 필수)&cpage=&rows=&shprfnm=`, 상세 `/pblprfr/{mt20id}`. 기간 `2026.11.28` 형식, 출연 `prfcast`는 "A, B 등" 문자열, 러닝타임 "1시간 30분" 글자, 줄거리 `sty`는 비는 경우가 많음(소개 이미지 `styurls`), 포스터는 http로 오지만 https로 바꿔도 열림, 장르는 `genrenm` 이름으로 매칭. 출처 문구 표시 필요.
   출처 표시, 키는 환경 변수로만. (2026-11-11 확정: KOBIS·국립중앙도서관은 포스터·줄거리·표지가 약하고 TMDB와 중복되어 제외)
6. ~~보낸 친구 요청 목록·취소, 작품 상세의 인물~~ (완료 2026-11-16: `Person`·`WorkPerson`(record 복합키) 엔티티, `findPeople` JPQL join on + DTO 생성자 식. KOPIS 가져오기 때 출연(최대 8)·제작진(최대 4)을 person/work_person 에 저장, 동명이인 합치지 않음).
**시간이 남으면**
7. 컬렉션 API·화면, 같이 볼 작품 추천(`db/04_queries.sql` Q12·Q13), 목록 후기 N+1 줄이기, `@Valid`, 별점 지우기, 에러 응답 통일, 간단한 테스트.
**마무리**
8. README(실행·배포), 시연 시나리오, 화면 캡처, `05_constraint_tests`·`04_queries` 결과 캡처, 360px 점검.

## 11. 알려진 주의점
- 샘플 데이터(작품·회원·후기)는 시연용 임의 값이다. 작품 정보는 TMDB·도서 API와 대조가 필요하다.
- Oracle은 내림차순 정렬에서 `NULL`을 앞에 둔다(위 10-3).
- 후기를 쓰면 DB 트리거가 기록 상태를 바꾸므로, 같은 트랜잭션 안에서 이미 읽어 둔 `record` 엔티티는 오래된 값일 수 있다(별도 요청으로 다시 읽으면 최신).
- 후기 건수: "1번 회원 후기"는 1번이 볼 수 있는 후기(본인+친구 공개) 수다. 원본 샘플 66건이 맞다(Oracle에서 테스트 후기 108·148을 빼고 세어 확인, Turso도 66). 1번이 직접 쓴 후기는 11건.
- Turso는 원본 샘플 데이터라 Oracle(테스트 데이터 남음)과 건수가 다르다. 비교는 원본 기준값(7번)으로 한다.
- Turso/SQLite: 날짜는 문자열로 저장되어 `@CreationTimestamp` 형식과 시드 형식이 섞이면 정렬이 틀어질 수 있다(2단계에서 확인). 외래 키는 연결마다 켜야 한다.
