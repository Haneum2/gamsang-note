-- ============================================================
-- 04_queries.sql : 핵심 쿼리 모음 (Oracle 12.2 이상)
-- 번호의 예시 값(1, 2)은 샘플 데이터의 사용자 번호다. 앱에서는 로그인한 사용자 번호로 바꾼다.
-- ============================================================

-- ------------------------------------------------------------
-- Q1. 친구 피드: 내가 볼 수 있는 친구들의 최근 후기 20건
-- ------------------------------------------------------------
SELECT nickname, work_type, title, rating, content, spoiler_yn, created_at
  FROM v_friend_feed
 WHERE viewer_id = 1
 ORDER BY created_at DESC
 FETCH FIRST 20 ROWS ONLY;

-- ------------------------------------------------------------
-- Q2. 공개 범위가 적용된 후기 조회: 1번 사용자가 볼 수 있는 후기 (본인 것 포함)
-- ------------------------------------------------------------
SELECT review_id, nickname, title, rating, visibility, created_at
  FROM v_visible_review
 WHERE viewer_id = 1
 ORDER BY created_at DESC
 FETCH FIRST 20 ROWS ONLY;

-- ------------------------------------------------------------
-- Q3. 월별 감상 수 (봤어요 상태, 감상일 기준)
-- ------------------------------------------------------------
SELECT TO_CHAR(watched_date, 'YYYY-MM') AS ym, COUNT(*) AS cnt
  FROM record
 WHERE user_id = 1
   AND status = 'DONE'
   AND watched_date IS NOT NULL
 GROUP BY TO_CHAR(watched_date, 'YYYY-MM')
 ORDER BY ym;

-- ------------------------------------------------------------
-- Q4. 장르별 평균 별점
-- ------------------------------------------------------------
SELECT g.name, ROUND(AVG(r.rating), 2) AS avg_rating, COUNT(*) AS cnt
  FROM record r
  JOIN work_genre wg ON wg.work_id  = r.work_id
  JOIN genre g       ON g.genre_id = wg.genre_id
 WHERE r.user_id = 1
   AND r.rating IS NOT NULL
 GROUP BY g.genre_id, g.name
 ORDER BY avg_rating DESC, cnt DESC;

-- ------------------------------------------------------------
-- Q5. 장르별 비율 (봤어요 기록 기준)
--     한 작품이 여러 장르를 가지면 장르마다 1건으로 세므로, 비율은 '장르 태그' 기준이다.
-- ------------------------------------------------------------
SELECT g.name,
       COUNT(*) AS cnt,
       ROUND(COUNT(*) * 100 / SUM(COUNT(*)) OVER (), 1) AS pct
  FROM record r
  JOIN work_genre wg ON wg.work_id  = r.work_id
  JOIN genre g       ON g.genre_id = wg.genre_id
 WHERE r.user_id = 1
   AND r.status = 'DONE'
 GROUP BY g.genre_id, g.name
 ORDER BY cnt DESC, g.name;

-- ------------------------------------------------------------
-- Q6. 연말 결산 리포트 (2026년)
-- ------------------------------------------------------------
-- (a) 올해 본 작품 수 (분야별)
SELECT w.type, COUNT(*) AS cnt
  FROM record r
  JOIN work w ON w.work_id = r.work_id
 WHERE r.user_id = 1
   AND r.status = 'DONE'
   AND r.watched_date >= DATE '2026-01-01'
   AND r.watched_date <  DATE '2027-01-01'
 GROUP BY w.type
 ORDER BY cnt DESC;

-- (b) 올해 선호 장르 TOP 3
SELECT g.name, COUNT(*) AS cnt
  FROM record r
  JOIN work_genre wg ON wg.work_id  = r.work_id
  JOIN genre g       ON g.genre_id = wg.genre_id
 WHERE r.user_id = 1
   AND r.status = 'DONE'
   AND r.watched_date >= DATE '2026-01-01'
   AND r.watched_date <  DATE '2027-01-01'
 GROUP BY g.genre_id, g.name
 ORDER BY cnt DESC, g.name
 FETCH FIRST 3 ROWS ONLY;

-- (c) 올해 최고 평점 작품 TOP 5
SELECT w.title, w.type, r.rating, r.watched_date
  FROM record r
  JOIN work w ON w.work_id = r.work_id
 WHERE r.user_id = 1
   AND r.status = 'DONE'
   AND r.rating IS NOT NULL
   AND r.watched_date >= DATE '2026-01-01'
   AND r.watched_date <  DATE '2027-01-01'
 ORDER BY r.rating DESC, r.watched_date
 FETCH FIRST 5 ROWS ONLY;

-- ------------------------------------------------------------
-- Q7. 친구 목록 (요청한 쪽/받은 쪽 양방향, 수락된 친구만)
-- ------------------------------------------------------------
SELECT u.user_id, u.nickname
  FROM friendship f
  JOIN users u ON u.user_id = CASE WHEN f.requester_id = 1 THEN f.receiver_id
                                   ELSE f.requester_id END
 WHERE f.status = 'ACCEPTED'
   AND 1 IN (f.requester_id, f.receiver_id)
 ORDER BY u.nickname;

-- ------------------------------------------------------------
-- Q8. 후기별 좋아요·댓글 수 (내가 쓴 후기)
-- ------------------------------------------------------------
SELECT v.review_id,
       w.title,
       v.visibility,
       (SELECT COUNT(*) FROM review_like l WHERE l.review_id = v.review_id) AS like_cnt,
       (SELECT COUNT(*) FROM comments c    WHERE c.review_id = v.review_id) AS comment_cnt
  FROM review v
  JOIN record r ON r.record_id = v.record_id
  JOIN work   w ON w.work_id   = r.work_id
 WHERE r.user_id = 1
 ORDER BY v.created_at DESC;

-- ------------------------------------------------------------
-- Q9. 작품 검색 (제목 일부 또는 인물 이름으로), 슈퍼타입 + 서브타입 조인
-- ------------------------------------------------------------
SELECT w.work_id, w.type, w.title, EXTRACT(YEAR FROM w.release_date) AS yr,
       m.runtime, b.pages, p.venue
  FROM work w
  LEFT JOIN movie       m ON m.work_id = w.work_id
  LEFT JOIN book        b ON b.work_id = w.work_id
  LEFT JOIN performance p ON p.work_id = w.work_id
 WHERE w.title LIKE '%' || '왕' || '%'
    OR w.work_id IN (SELECT wp.work_id
                       FROM work_person wp
                       JOIN person pe ON pe.person_id = wp.person_id
                      WHERE pe.name LIKE '%' || '왕' || '%')
 ORDER BY w.type, w.title;

-- ------------------------------------------------------------
-- Q10. 작품별 장르와 참여 인물 (N:M 두 개를 각각 묶어서 조회)
-- ------------------------------------------------------------
SELECT w.title,
       (SELECT LISTAGG(g.name, ', ') WITHIN GROUP (ORDER BY g.name)
          FROM work_genre wg JOIN genre g ON g.genre_id = wg.genre_id
         WHERE wg.work_id = w.work_id) AS genres,
       (SELECT LISTAGG(pe.name || '(' || wp.role || ')', ', ') WITHIN GROUP (ORDER BY wp.role, pe.name)
          FROM work_person wp JOIN person pe ON pe.person_id = wp.person_id
         WHERE wp.work_id = w.work_id) AS people
  FROM work w
 WHERE w.type = 'MOVIE'
 ORDER BY w.title
 FETCH FIRST 10 ROWS ONLY;

-- ------------------------------------------------------------
-- Q11. 컬렉션과 담긴 작품
-- ------------------------------------------------------------
SELECT c.collection_id, u.nickname, c.title AS collection_title, w.type, w.title
  FROM collection c
  JOIN users u            ON u.user_id       = c.user_id
  JOIN collection_item ci ON ci.collection_id = c.collection_id
  JOIN work w             ON w.work_id        = ci.work_id
 ORDER BY c.collection_id, w.title;

-- ------------------------------------------------------------
-- Q12. (부가) 같이 볼 작품 - 기본형: 나(1)와 친구(2) 둘 다 '봤어요'가 아닌 작품
-- ------------------------------------------------------------
SELECT w.work_id, w.type, w.title
  FROM work w
 WHERE NOT EXISTS (SELECT 1
                     FROM record r
                    WHERE r.work_id = w.work_id
                      AND r.user_id IN (1, 2)
                      AND r.status = 'DONE')
 ORDER BY w.type, w.title;

-- ------------------------------------------------------------
-- Q13. (부가) 같이 볼 작품 - 추천형
--      두 사람이 모두 평균 3.5점 이상 준 장르(공통 선호 장르)에 속하고,
--      둘 다 아직 '봤어요'가 아닌 작품을 골라
--      겹치는 선호 장르 수 → 전체 이용자 평균 별점 순으로 10건 추천한다.
-- ------------------------------------------------------------
WITH pref AS (
  SELECT r.user_id, wg.genre_id
    FROM record r
    JOIN work_genre wg ON wg.work_id = r.work_id
   WHERE r.user_id IN (1, 2)
     AND r.rating IS NOT NULL
   GROUP BY r.user_id, wg.genre_id
  HAVING AVG(r.rating) >= 3.5
),
common AS (
  SELECT genre_id
    FROM pref
   GROUP BY genre_id
  HAVING COUNT(DISTINCT user_id) = 2
)
SELECT w.work_id, w.type, w.title,
       COUNT(DISTINCT wg.genre_id)       AS match_genres,
       ROUND(AVG(rr.rating), 2)          AS avg_rating,
       COUNT(DISTINCT rr.record_id)      AS rating_cnt
  FROM work w
  JOIN work_genre wg ON wg.work_id = w.work_id
                    AND wg.genre_id IN (SELECT genre_id FROM common)
  LEFT JOIN record rr ON rr.work_id = w.work_id AND rr.rating IS NOT NULL
 WHERE NOT EXISTS (SELECT 1
                     FROM record r
                    WHERE r.work_id = w.work_id
                      AND r.user_id IN (1, 2)
                      AND r.status = 'DONE')
 GROUP BY w.work_id, w.type, w.title
 ORDER BY match_genres DESC, avg_rating DESC NULLS LAST, w.title
 FETCH FIRST 10 ROWS ONLY;
