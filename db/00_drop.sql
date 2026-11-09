-- ============================================================
-- 00_drop.sql : 재실행용 초기화 (기존 객체 삭제)
-- 처음 실행하는 경우에는 건너뛰어도 된다.
-- ============================================================
BEGIN
  FOR v IN (SELECT view_name FROM user_views
             WHERE view_name IN ('V_FRIEND_FEED', 'V_VISIBLE_REVIEW')) LOOP
    EXECUTE IMMEDIATE 'DROP VIEW ' || v.view_name;
  END LOOP;

  FOR t IN (SELECT table_name FROM user_tables
             WHERE table_name IN ('COLLECTION_ITEM', 'COLLECTION', 'COMMENTS',
                                  'REVIEW_LIKE', 'REVIEW', 'RECORD',
                                  'WORK_PERSON', 'PERSON', 'WORK_GENRE', 'GENRE',
                                  'PERFORMANCE', 'BOOK', 'MOVIE', 'WORK',
                                  'FRIENDSHIP', 'USERS')) LOOP
    EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS PURGE';
  END LOOP;
END;
/
