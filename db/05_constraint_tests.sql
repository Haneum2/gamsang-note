-- ============================================================
-- 05_constraint_tests.sql : 제약조건·트리거 동작 확인 (보고서 캡처용)
-- 01~03 실행 후 SQL Developer 또는 SQL*Plus에서 한 문장씩 실행한다.
-- 각 문장 위 주석이 예상 결과다. 오류가 나는 것이 정상인 문장이다.
-- ============================================================
SET SERVEROUTPUT ON;

-- [1] CHECK: 별점은 0.5 단위만 허용 → ORA-02290 (CK_RECORD_RATING 위반)
INSERT INTO record (user_id, work_id, status, rating)
VALUES (1, (SELECT MIN(work_id) FROM work
             WHERE work_id NOT IN (SELECT work_id FROM record WHERE user_id = 1)),
        'DONE', 4.3);

-- [2] CHECK: 별점 범위(0.5~5.0) 초과 → ORA-02290 (CK_RECORD_RATING 위반)
INSERT INTO record (user_id, work_id, status, rating)
VALUES (1, (SELECT MIN(work_id) FROM work
             WHERE work_id NOT IN (SELECT work_id FROM record WHERE user_id = 1)),
        'DONE', 5.5);

-- [3] UNIQUE: 같은 사용자가 같은 작품을 두 번 기록 → ORA-00001 (UQ_RECORD_USER_WORK)
INSERT INTO record (user_id, work_id)
SELECT user_id, work_id FROM record WHERE record_id = 1;

-- [4] UNIQUE: 반대 방향 친구 요청 (샘플에 1→2 요청이 있음) → ORA-00001 (UQ_FRIENDSHIP_PAIR)
INSERT INTO friendship (requester_id, receiver_id) VALUES (2, 1);

-- [5] CHECK: 자기 자신에게 친구 요청 → ORA-02290 (CK_FRIENDSHIP_NOT_SELF)
INSERT INTO friendship (requester_id, receiver_id) VALUES (5, 5);

-- [6] CHECK: 허용되지 않는 공개 범위 값 → ORA-02290 (CK_REVIEW_VISIBILITY)
INSERT INTO review (record_id, content, visibility) VALUES (1, '테스트', 'EVERYONE');

-- [7] 삭제 규칙(RESTRICT): 기록이 있는 작품은 삭제 불가 → ORA-02292 (FK_RECORD_WORK)
DELETE FROM work WHERE work_id = (SELECT MIN(work_id) FROM record);

-- [8] 삭제 규칙(CASCADE): 사용자를 지우면 기록·후기·좋아요 등이 함께 삭제됨 (마지막에 ROLLBACK)
SELECT COUNT(*) AS before_cnt FROM record WHERE user_id = 12;      -- 0보다 큼
DELETE FROM users WHERE user_id = 12;
SELECT COUNT(*) AS after_cnt  FROM record WHERE user_id = 12;      -- 0
ROLLBACK;

-- [9] 트리거: 후기를 쓰면 '보고 싶어요' 기록이 '봤어요(DONE)'로 바뀜 (ROLLBACK으로 원복)
DECLARE
  v_rid    record.record_id%TYPE;
  v_before record.status%TYPE;
  v_after  record.status%TYPE;
BEGIN
  SELECT record_id, status INTO v_rid, v_before
    FROM (SELECT record_id, status FROM record WHERE status = 'WANT' ORDER BY record_id)
   WHERE ROWNUM = 1;

  INSERT INTO review (record_id, content, visibility) VALUES (v_rid, '트리거 테스트', 'PRIVATE');

  SELECT status INTO v_after FROM record WHERE record_id = v_rid;
  DBMS_OUTPUT.PUT_LINE('record ' || v_rid || ': ' || v_before || ' -> ' || v_after);  -- WANT -> DONE
  ROLLBACK;
END;
/
