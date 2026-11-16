package com.gamsang.note;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * prod(Turso) 전용. libSQL JDBC 드라이버(HTTP 방식)는 setAutoCommit/commit/rollback 을 지원하지 않고
 * 호출하면 SQLFeatureNotSupportedException 을 던진다(1.0.4~1.0.9 동일). 그러면 @Transactional 이 시작조차 못 한다.
 * 그래서 커넥션을 감싸 이 세 호출을 "아무 일도 안 함"으로 바꾸고, 인자가 더 붙은 prepareStatement 도 기본 버전으로 돌린다.
 * ponytail: 모든 문장이 즉시 커밋된다(자동 커밋). 서비스 메서드 중간에 오류가 나도 앞 문장은 되돌아가지 않는다.
 *           우리 쓰기는 대부분 1문장이라 시연엔 충분. 트랜잭션이 꼭 필요하면 DB를 바꿔야 한다(docs/DEPLOY.md 6번).
 */
@Configuration
@Profile("prod")
public class TursoConfig {

    private static final Set<String> NO_OP = Set.of("setAutoCommit", "commit", "rollback");

    // static: BeanPostProcessor 는 다른 빈보다 먼저 만들어져야 해서 static @Bean 으로 둔다.
    @Bean
    static BeanPostProcessor tursoAutoCommitOnly() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String name) {
                if (!(bean instanceof DataSource ds)) return bean;
                return new DelegatingDataSource(ds) {
                    @Override
                    public Connection getConnection() throws SQLException {
                        return wrap(super.getConnection());
                    }
                };
            }
        };
    }

    private static Connection wrap(Connection real) {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] {Connection.class},
                (proxy, method, args) -> {
                    if (NO_OP.contains(method.getName())) return null;
                    if (method.getName().equals("getAutoCommit")) return true;
                    // prepareStatement(sql, 결과셋 종류, 동시성) 같은 추가 인자 버전도 미지원이라 기본 버전으로 돌린다.
                    // Hibernate 가 넘기는 값은 기본값(FORWARD_ONLY, READ_ONLY)이라 동작이 같다.
                    // 새 키는 INSERT ... RETURNING 으로 받으므로(TursoDialect) 생성키 인자는 필요 없다.
                    if (method.getName().equals("prepareStatement")) {
                        return wrap(real.prepareStatement((String) args[0]));
                    }
                    return call(real, method, args);
                });
    }

    // PreparedStatement 는 executeLargeUpdate() 는 되는데 executeUpdate() 가 미지원으로 남아 있다(드라이버 누락).
    // 게다가 돌려주는 값이 "바뀐 행 수"가 아니라 rows_written(인덱스 쓰기까지 센 값)이라 1행 UPDATE 도 2·3이 나온다.
    // Hibernate 는 엔티티 1건 INSERT/UPDATE/DELETE 가 정확히 1행이길 검사하므로 0 → 0, 그 밖 → 1 로 바로잡는다.
    // ponytail: 여러 행을 바꾸는 @Modifying 벌크 쿼리를 추가하면 건수가 1로 보인다. 그땐 이 보정을 다시 봐야 한다.
    private static PreparedStatement wrap(PreparedStatement real) {
        return (PreparedStatement) Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(),
                new Class<?>[] {PreparedStatement.class}, (proxy, method, args) -> {
                    if (method.getName().equals("executeUpdate") && method.getParameterCount() == 0) {
                        return real.executeLargeUpdate() > 0 ? 1 : 0;
                    }
                    return call(real, method, args);
                });
    }

    private static Object call(Object target, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause(); // SQLException 을 그대로 전달
        }
    }
}
