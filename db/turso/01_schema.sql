-- ============================================================
-- turso/01_schema.sql : Turso(libSQL = SQLite) 용 테이블 (16개)
-- Oracle 용 db/01_schema.sql 과 같은 테이블·제약조건을 SQLite 문법으로 옮긴 것.
-- 옮길 때 바뀐 점
--   NUMBER/IDENTITY -> INTEGER PRIMARY KEY (자동 번호)
--   VARCHAR2/CLOB   -> TEXT,  DATE/TIMESTAMP -> TEXT ('YYYY-MM-DD' / 'YYYY-MM-DD HH:MM:SS')
--   NUMBER(2,1)     -> REAL
-- 외래 키는 연결마다 PRAGMA foreign_keys = ON 이 켜져 있어야 동작한다(Turso 는 기본 ON).
-- ============================================================
CREATE TABLE users (user_id INTEGER PRIMARY KEY, nickname TEXT NOT NULL, email TEXT NOT NULL UNIQUE, created_at TEXT NOT NULL);
CREATE TABLE friendship (
  requester_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  receiver_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','ACCEPTED')),
  requested_at TEXT NOT NULL,
  PRIMARY KEY (requester_id, receiver_id),
  CHECK (requester_id <> receiver_id));
CREATE UNIQUE INDEX uq_friendship_pair ON friendship (min(requester_id, receiver_id), max(requester_id, receiver_id));
CREATE TABLE work (work_id INTEGER PRIMARY KEY, type TEXT NOT NULL CHECK (type IN ('MOVIE','BOOK','PERFORMANCE')),
  title TEXT NOT NULL, release_date TEXT, summary TEXT, poster_url TEXT);
CREATE TABLE movie (work_id INTEGER PRIMARY KEY REFERENCES work(work_id) ON DELETE CASCADE, runtime INTEGER CHECK (runtime IS NULL OR runtime > 0));
CREATE TABLE book (work_id INTEGER PRIMARY KEY REFERENCES work(work_id) ON DELETE CASCADE, isbn TEXT UNIQUE, pages INTEGER CHECK (pages IS NULL OR pages > 0));
CREATE TABLE performance (work_id INTEGER PRIMARY KEY REFERENCES work(work_id) ON DELETE CASCADE, venue TEXT, period TEXT);
CREATE TABLE genre (genre_id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE);
CREATE TABLE work_genre (work_id INTEGER NOT NULL REFERENCES work(work_id) ON DELETE CASCADE,
  genre_id INTEGER NOT NULL REFERENCES genre(genre_id) ON DELETE CASCADE, PRIMARY KEY (work_id, genre_id));
CREATE TABLE person (person_id INTEGER PRIMARY KEY, name TEXT NOT NULL);
CREATE TABLE work_person (work_id INTEGER NOT NULL REFERENCES work(work_id) ON DELETE CASCADE,
  person_id INTEGER NOT NULL REFERENCES person(person_id) ON DELETE CASCADE, role TEXT NOT NULL,
  PRIMARY KEY (work_id, person_id, role));
CREATE TABLE record (record_id INTEGER PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  work_id INTEGER NOT NULL REFERENCES work(work_id),
  status TEXT NOT NULL DEFAULT 'WANT' CHECK (status IN ('WANT','WATCHING','DONE')),
  rating REAL CHECK (rating IS NULL OR (rating BETWEEN 0.5 AND 5.0 AND (rating * 10) % 5 = 0)),
  watched_date TEXT, UNIQUE (user_id, work_id));
CREATE TABLE review (review_id INTEGER PRIMARY KEY,
  record_id INTEGER NOT NULL REFERENCES record(record_id) ON DELETE CASCADE,
  content TEXT NOT NULL, spoiler_yn TEXT NOT NULL DEFAULT 'N' CHECK (spoiler_yn IN ('Y','N')),
  visibility TEXT NOT NULL DEFAULT 'PRIVATE' CHECK (visibility IN ('ALL','FRIEND','PRIVATE')),
  created_at TEXT NOT NULL);
CREATE TABLE review_like (review_id INTEGER NOT NULL REFERENCES review(review_id) ON DELETE CASCADE,
  user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE, created_at TEXT NOT NULL,
  PRIMARY KEY (review_id, user_id));
CREATE TABLE comments (comment_id INTEGER PRIMARY KEY,
  review_id INTEGER NOT NULL REFERENCES review(review_id) ON DELETE CASCADE,
  user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  content TEXT NOT NULL, created_at TEXT NOT NULL);
CREATE TABLE collection (collection_id INTEGER PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE, title TEXT NOT NULL);
CREATE TABLE collection_item (collection_id INTEGER NOT NULL REFERENCES collection(collection_id) ON DELETE CASCADE,
  work_id INTEGER NOT NULL REFERENCES work(work_id) ON DELETE CASCADE, PRIMARY KEY (collection_id, work_id));
