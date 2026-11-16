-- 07_add_password_hash.sql : 이미 올린 Turso DB에 비밀번호 해시 컬럼 추가 (2026-11-16)
-- 새로 올리는 DB(gamsang.db / gamsang_dump.sql / 01_schema.sql)에는 이미 들어 있다.
-- SQLite 는 ALTER 로 NOT NULL 을 나중에 걸 수 없어서(DEFAULT 를 주면 가능하지만 그러면 빠뜨린 INSERT 가 데모 비밀번호로
-- 들어간다) 이 DB에서는 NULL 허용으로 둔다. 앱은 가입할 때 항상 해시를 넣는다.
ALTER TABLE users ADD COLUMN password_hash TEXT;
UPDATE users SET password_hash = '$2a$10$LXrvx3OPZmrSHDnTI.I9DeNzo.jI5Yav86JauzJzdpYdZJFc2O9p.';
