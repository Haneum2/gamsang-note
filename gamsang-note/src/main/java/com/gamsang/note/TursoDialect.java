package com.gamsang.note;

import org.hibernate.community.dialect.SQLiteDialect;

/**
 * prod(Turso) 전용 방언. libSQL 드라이버는 prepareStatement(sql, 생성키 반환)를 지원하지 않아
 * IDENTITY 키로 INSERT 하면 실패한다. SQLite(3.35+)/libSQL 은 INSERT ... RETURNING 을 지원하므로,
 * 그렇다고 알려 주면 Hibernate 가 "insert ... returning record_id" 한 문장으로 새 키를 받아 온다.
 */
public class TursoDialect extends SQLiteDialect {
    @Override
    public boolean supportsInsertReturning() {
        return true;
    }
}
