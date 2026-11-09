-- ============================================================
-- 03_views_triggers.sql : 뷰와 트리거 (Oracle 12.2 이상)
-- 01_schema.sql, 02_seed.sql 실행 후에 실행한다.
-- (트리거를 샘플 데이터 입력 뒤에 만드는 이유: 입력 중 상태값이 바뀌지 않게 하기 위함)
-- ============================================================

-- ------------------------------------------------------------
-- 1) 친구 피드 뷰
--    뷰는 매개변수를 받을 수 없으므로 '보는 사람(viewer_id)'을 컬럼으로 두고
--    호출할 때 WHERE viewer_id = :나 로 거른다.
--    친구 관계는 요청한 쪽/받은 쪽을 모두 보도록 양방향으로 펼쳤고,
--    공개 범위가 ALL 또는 FRIEND 인 후기만 나온다.
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_friend_feed AS
SELECT f.viewer_id,
       u.user_id   AS friend_id,
       u.nickname,
       v.review_id,
       w.work_id,
       w.type      AS work_type,
       w.title,
       r.rating,
       v.content,
       v.spoiler_yn,
       v.visibility,
       v.created_at
  FROM (SELECT requester_id AS viewer_id, receiver_id AS friend_id
          FROM friendship WHERE status = 'ACCEPTED'
        UNION ALL
        SELECT receiver_id, requester_id
          FROM friendship WHERE status = 'ACCEPTED') f
  JOIN record r ON r.user_id   = f.friend_id
  JOIN review v ON v.record_id = r.record_id
  JOIN work   w ON w.work_id   = r.work_id
  JOIN users  u ON u.user_id   = r.user_id
 WHERE v.visibility IN ('ALL', 'FRIEND');

-- ------------------------------------------------------------
-- 2) 공개 범위가 적용된 후기 조회 뷰
--    viewer_id 가 볼 수 있는 후기만 나온다.
--      - 내가 쓴 후기: 공개 범위와 관계없이 모두
--      - 전체(ALL)    : 누구나
--      - 친구(FRIEND) : 수락된 친구만 (요청 방향 무관)
--      - 나만 보기(PRIVATE): 작성자 본인만
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_visible_review AS
SELECT vw.user_id AS viewer_id,
       v.review_id,
       a.user_id  AS author_id,
       a.nickname,
       w.work_id,
       w.type     AS work_type,
       w.title,
       r.rating,
       v.content,
       v.spoiler_yn,
       v.visibility,
       v.created_at
  FROM review v
  JOIN record r ON r.record_id = v.record_id
  JOIN work   w ON w.work_id   = r.work_id
  JOIN users  a ON a.user_id   = r.user_id
  JOIN users vw ON vw.user_id  = r.user_id
               OR v.visibility = 'ALL'
               OR (v.visibility = 'FRIEND'
                   AND EXISTS (SELECT 1
                                 FROM friendship f
                                WHERE f.status = 'ACCEPTED'
                                  AND ((f.requester_id = vw.user_id AND f.receiver_id  = r.user_id)
                                    OR (f.receiver_id  = vw.user_id AND f.requester_id = r.user_id))));

-- ------------------------------------------------------------
-- 3) 트리거: 후기를 쓰면 해당 기록의 상태를 '봤어요(DONE)'로 바꾼다 (제안서의 선택 기능)
-- ------------------------------------------------------------
CREATE OR REPLACE TRIGGER trg_review_mark_done
AFTER INSERT ON review
FOR EACH ROW
BEGIN
  UPDATE record
     SET status = 'DONE'
   WHERE record_id = :NEW.record_id;
END;
/

-- (참고) MySQL 문서의 두 번째 트리거 trg_friendship_no_reverse(반대 방향 친구 요청 차단)는
-- 01_schema.sql 끝의 함수 기반 유일 인덱스 uq_friendship_pair 로 대체했다.
