# 배포 가이드 (Turso + Render)

팀 결정(11/11): 배포는 **Render**, 클라우드 DB는 **Turso**. 기존 Oracle 작업과 충돌하지 않도록 **프로필 2개**로 나눴다.

| | 기본(default) | prod |
| --- | --- | --- |
| 용도 | 로컬 개발, 보고서 기준 DB | 배포 |
| DB | Oracle XE (`db/*.sql`) | Turso(libSQL) (`db/turso/*.sql`) |
| 실행 | `mvn spring-boot:run` | `SPRING_PROFILES_ACTIVE=prod` |

> **주의**: Turso 쪽은 아직 실제 접속으로 시험하지 못했다. 처음 접속이 되는지부터 확인하는 단계(5번)가 필요하다.

## 1. Turso 준비
1. https://turso.tech 가입.
2. Turso CLI는 Windows에서 **WSL(Ubuntu)** 안에서만 동작한다. 설치가 번거로우면 Turso 웹 대시보드에서 DB를 만들고 `db/turso/*.sql`을 실행해도 된다(대시보드의 SQL 콘솔 이용, 파일이 크면 02_seed를 나눠 붙여넣기).
3. (CLI 사용 시) `turso auth login` → 아래 방법 중 하나.
   - 파일째 올리기: `turso db create gamsang --from-file db/turso/gamsang.db` (WAL 모드로 만들어 둠, 2GB 이하)
   - 덤프로 올리기: `turso db create gamsang --from-dump db/turso/gamsang_dump.sql`
4. 접속 정보 확인: `turso db show gamsang --url` (예: `libsql://gamsang-계정.turso.io`), `turso db tokens create gamsang` (토큰).
5. 확인: `turso db shell gamsang "SELECT COUNT(*) FROM work;"` → 105.

접속 주소와 토큰은 **파일·채팅·깃허브에 올리지 않는다.**

## 2. 로컬에서 prod 프로필 시험 (Render 전에)
Windows PowerShell:
```
$env:SPRING_PROFILES_ACTIVE="prod"
$env:TURSO_JDBC_URL="jdbc:dbeaver:libsql:https://gamsang-계정.turso.io"   # libsql:// 을 https:// 로 바꾼다
$env:TURSO_AUTH_TOKEN="(토큰)"
cd gamsang-note
mvn spring-boot:run
```
브라우저에서 `http://localhost:8080/api/genres` 가 21건이면 성공. 안 되면 콘솔의 첫 오류 줄을 확인한다(6번 참고).

## 3. Render 배포
1. 이 폴더를 GitHub 저장소로 올린다(`.gitignore` 확인, 비밀 값 없음).
2. Render → New → **Blueprint** → 저장소 선택 → `render.yaml` 인식.
3. 환경 변수 `TURSO_JDBC_URL`, `TURSO_AUTH_TOKEN` 값을 Render 화면에서 입력.
4. 배포가 끝나면 `https://gamsang-note.onrender.com` 형태의 주소로 화면과 API가 함께 열린다(서비스 1개, 프론트는 Spring Boot가 함께 서빙).

## 4. 무료 플랜 특성 (시연 전에 알아 둘 것)
- 15분간 요청이 없으면 잠들고, 다음 접속 때 **약 1분** 걸려 깨어난다. **시연 5분 전에 한 번 열어 둔다.**
- 디스크가 임시라서 파일 저장은 못 한다(우리는 DB를 Turso에 두므로 문제 없음).
- 메모리 512MB: Dockerfile에서 `-Xmx300m`으로 제한했다.

## 5. 처음 확인할 것(미검증 위험)
1. libSQL JDBC 드라이버가 Spring/Hibernate와 연결되는지(시작 시 오류 여부).
2. 날짜(`LocalDate`, `LocalDateTime`) 저장·조회가 맞는지: 작품 상세의 개봉일, 후기 작성 시각, 월별 통계.
3. 후기 작성 시 트리거가 동작해 기록 상태가 `DONE`이 되는지.
4. 같은 요청을 Oracle(로컬)과 Turso(prod)에서 호출해 결과가 같은지.
5. 외래 키 제약이 켜져 있는지(없는 작품으로 기록 등록 시 오류가 나야 함).

## 6. 막혔을 때 선택지
- 드라이버 연결이 안 되면: Hikari 설정(`connection-test-query`)과 `allow_jdbc_metadata_access`를 먼저 점검 → 그래도 안 되면 팀과 상의해 Render PostgreSQL 등 다른 DB로 바꾸거나 Turso의 HTTP API를 직접 호출하는 방식을 검토한다.
- 어느 경우에도 **보고서의 논리·물리 모델은 Oracle 기준 그대로**이고, Turso 스크립트는 "배포용 이식본"으로 부록에 설명한다.

## 7. 두 DB 스크립트를 함께 관리하는 규칙
테이블·컬럼을 바꾸면 `db/01_schema.sql`(Oracle)과 `db/turso/01_schema.sql`을 **같이** 고친다. 엔티티에서 `@Lob`은 쓰지 않는다(SQLite 호환).
