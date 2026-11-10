# 감상노트

영화·책·공연을 기록하고 후기를 공유하는 앱. (데이터베이스 응용 팀 프로젝트)

스택: Spring Boot 3 + JPA + Oracle XE 21c + React(Vite). 배포용은 Render + Turso(SQLite 계열).

> 간단 안내판이다. **팀원은 먼저 `docs/TEAM.md`(역할 분담, 일정, 규칙)를 읽는다.** 배포 주소, 가입·로그인 사용법, 시연 시나리오는 완성 단계에서 보강한다.

## 1. 미리 설치할 것
| 프로그램 | 버전 | 용도 |
| --- | --- | --- |
| Java | 17 | 백엔드 |
| Maven | 3.9 이상 | 백엔드 빌드(IntelliJ에 포함된 것도 가능) |
| Node.js | 22 | 프론트 |
| Oracle XE | 21c | DB (서비스명 `XEPDB1`) |
| SQL Developer | 최신 | DB 스크립트 실행 |

## 2. DB 만들기 (처음 한 번)
SQL Developer에서 `SYSTEM` 계정으로 접속(`localhost:1521`, 서비스명 `XEPDB1`)한 뒤 순서대로 실행한다.

1. `db/01_schema.sql`
2. `db/02_seed.sql`
3. `db/03_views_triggers.sql`

다시 만들 때는 `db/00_drop.sql`을 먼저 실행한다. 정상이면 회원 12, 작품 105, 감상 기록 244, 후기 107이다.

## 3. 백엔드 실행 (포트 8080)
DB 비밀번호는 파일에 적지 않고 환경 변수로만 넘긴다. 비밀번호는 개발 담당에게 받는다.

PowerShell:
```
$env:DB_PASSWORD="(받은 비밀번호)"
cd gamsang-note
mvn spring-boot:run
```
`mvn`이 안 되면 IntelliJ로 `NoteApplication`을 실행하고 실행 설정의 환경 변수에 `DB_PASSWORD`를 넣는다.

확인: 브라우저에서 http://localhost:8080/api/genres 를 열어 장르 21건이 보이면 성공.

## 4. 프론트 실행 (포트 5173)
새 터미널에서:
```
cd gamsang-note-web
npm install      # 처음 한 번
npm run dev
```
http://localhost:5173 을 연다. `/api` 요청은 자동으로 8080으로 전달된다.

현재는 로그인이 없어 화면 위쪽에서 회원(나)을 고른다. 자체 가입이 들어가면 이 부분이 바뀐다.

## 5. 문제가 생기면
| 증상 | 확인 |
| --- | --- |
| 백엔드가 `ORA-01017` 오류 | `DB_PASSWORD`가 틀렸거나 이 터미널에 설정되지 않음 |
| 백엔드가 `Connection refused` | Oracle 서비스(`OracleServiceXE`)와 리스너가 꺼져 있음 |
| 화면에 "서버에 연결할 수 없음" | 백엔드(8080)가 꺼져 있음 |
| 8080 포트 사용 중 | 이미 백엔드가 실행 중인지 확인 |
| 한글 JSON을 curl로 보내면 400 | 본문을 UTF-8 파일로 저장해 `--data-binary @파일`로 전송 |

## 6. 테스트 담당 참고
- 버그를 찾으면 "재현 순서 / 기대 결과 / 실제 결과"로 적어 개발 담당에게 전달한다.
- 이전 테스트 데이터가 DB에 남아 있어 건수가 조금 더 많을 수 있다(기록 285 등). 정상이다.
- 비밀번호·토큰·API 키는 단톡방이나 저장소에 올리지 않는다.

## 7. 배포 (Render + Turso)
배포 절차와 무료 플랜 절전(시연 전 준비) 안내는 `docs/DEPLOY.md`를 본다. 배포가 끝나면 이 README에 주소와 시연 전 체크리스트를 추가한다.
