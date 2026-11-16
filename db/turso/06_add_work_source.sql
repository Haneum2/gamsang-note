-- 06_add_work_source.sql : 이미 올린 Turso DB에 외부 작품 ID 컬럼 추가 (2026-11-16)
-- 새로 올리는 DB(gamsang.db / gamsang_dump.sql / 01_schema.sql)에는 이미 들어 있다.
-- SQLite의 ALTER TABLE 은 표 단위 제약을 추가할 수 없어서, 짝 CHECK 는 열에 붙이고 UNIQUE 는 인덱스로 만든다.
ALTER TABLE work ADD COLUMN source TEXT CHECK (source IN ('TMDB','YES24','KOPIS'));
ALTER TABLE work ADD COLUMN external_id TEXT
  CHECK ((source IS NULL AND external_id IS NULL) OR (source IS NOT NULL AND external_id IS NOT NULL));
CREATE UNIQUE INDEX uq_work_source_ext ON work (source, external_id);
