-- ============================================================
-- 06_add_work_source.sql : 이미 만들어 둔 DB에 외부 작품 ID 컬럼을 추가한다 (2026-11-16)
-- 새로 만드는 DB는 01_schema.sql 에 이미 들어 있으니 실행하지 않는다.
-- 기존 행은 두 컬럼이 NULL 로 들어가며 데이터는 지워지지 않는다.
-- ============================================================
ALTER TABLE work ADD (
  source       VARCHAR2(10) NULL,
  external_id  VARCHAR2(40) NULL,
  CONSTRAINT ck_work_source CHECK (source IN ('TMDB', 'YES24', 'KOPIS')),
  CONSTRAINT ck_work_source_pair CHECK ((source IS NULL AND external_id IS NULL)
                                     OR (source IS NOT NULL AND external_id IS NOT NULL)),
  CONSTRAINT uq_work_source_ext UNIQUE (source, external_id)
);
