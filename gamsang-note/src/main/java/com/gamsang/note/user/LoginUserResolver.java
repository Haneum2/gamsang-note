package com.gamsang.note.user;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 가 컨트롤러 파라미터 값을 채울 때(@RequestParam, @PathVariable 처럼) 쓰는 "인자 해석기"를 하나 더 등록한다.
 * @LoginUser Long 파라미터를 만나면 세션의 회원 번호를 넣고, 없으면 401.
 */
@Configuration
public class LoginUserResolver implements HandlerMethodArgumentResolver, WebMvcConfigurer {

    /** 세션에 회원 번호를 담는 이름 (AuthController 가 로그인할 때 넣는다) */
    public static final String SESSION_KEY = "userId";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class) && parameter.getParameterType() == Long.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mav,
                                  NativeWebRequest request, WebDataBinderFactory binderFactory) {
        // SCOPE_SESSION 으로 읽으면 세션이 없을 때 새로 만들지 않는다.
        Object id = request.getAttribute(SESSION_KEY, RequestAttributes.SCOPE_SESSION);
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        return id;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this);
    }
}
