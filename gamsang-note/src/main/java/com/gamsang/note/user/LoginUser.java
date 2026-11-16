package com.gamsang.note.user;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드 파라미터에 붙이면 "로그인한 회원 번호(Long)"가 들어온다.
 *   public Page<...> list(@LoginUser Long me) { ... }
 * 로그인하지 않았으면 메서드가 실행되기 전에 401 을 돌려준다. (LoginUserResolver 가 처리)
 * 예전처럼 요청에 userId 를 담아 보내면 남의 번호로도 요청할 수 있었는데, 이제는 서버 세션에서만 꺼낸다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
