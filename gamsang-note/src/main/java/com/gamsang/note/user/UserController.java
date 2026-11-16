package com.gamsang.note.user;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원 목록(번호·닉네임). 친구 요청 상대를 고르는 데 쓴다. 로그인한 회원만 볼 수 있다.
    @GetMapping
    public List<UserResponse> list(@LoginUser Long me) {
        return userRepository.findAll(Sort.by("id")).stream()
                .map(u -> new UserResponse(u.getId(), u.getNickname()))
                .toList();
    }

    public record UserResponse(Long id, String nickname) {
    }
}
