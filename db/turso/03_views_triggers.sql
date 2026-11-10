-- ============================================================
-- turso/03_views_triggers.sql : 뷰 2개 + 트리거 1개 (02_seed.sql 실행 뒤에 실행)
-- 내용은 Oracle 용 db/03_views_triggers.sql 과 같고 SQLite 문법만 다르다.
-- ============================================================
CREATE VIEW v_visible_review AS
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

CREATE VIEW v_friend_feed AS
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

CREATE TRIGGER trg_review_mark_done
AFTER INSERT ON review
FOR EACH ROW
BEGIN
  UPDATE record SET status = 'DONE' WHERE record_id = NEW.record_id;
END;
