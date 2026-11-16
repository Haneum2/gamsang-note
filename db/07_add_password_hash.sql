-- ============================================================
-- 07_add_password_hash.sql : 이미 만들어 둔 DB에 비밀번호 해시 컬럼을 추가한다 (2026-11-16, 자체 가입/로그인)
-- 새로 만드는 DB는 01_schema.sql / 02_seed.sql 에 이미 들어 있으니 실행하지 않는다.
-- 기존 회원(샘플·테스트)은 모두 데모 비밀번호 'gamsang1234' 의 BCrypt 해시로 채운다.
-- ============================================================
ALTER TABLE users ADD (password_hash VARCHAR2(100));
UPDATE users SET password_hash = '$2a$10$LXrvx3OPZmrSHDnTI.I9DeNzo.jI5Yav86JauzJzdpYdZJFc2O9p.';
ALTER TABLE users MODIFY (password_hash NOT NULL);
COMMIT;
