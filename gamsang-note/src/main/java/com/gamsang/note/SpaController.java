package com.gamsang.note;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 배포(prod)에서는 React 빌드 결과물(static/index.html)을 Spring Boot 가 함께 서빙한다.
 * 브라우저에서 /records 처럼 React 라우터 경로로 직접 들어오거나 새로고침하면
 * 서버에는 그런 파일이 없어 404 가 되므로, 확장자 없는 경로는 index.html 로 넘겨 준다.
 * (/api/** 는 각 컨트롤러가 먼저 처리하므로 영향이 없다. 화면 경로가 한 단계(/records)인 동안만 쓸 수 있다.)
 */
@Controller
public class SpaController {

    @GetMapping(value = { "/", "/{path:[^\\.]*}" })
    public String forward() {
        return "forward:/index.html";
    }
}
