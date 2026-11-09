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

    // 로그인 기능이 생기기 전까지 '내가 누구인지' 고르기 위한 회원 목록
    @GetMapping
    public List<UserResponse> list() {
        return userRepository.findAll(Sort.by("id")).stream()
                .map(u -> new UserResponse(u.getId(), u.getNickname()))
                .toList();
    }

    public record UserResponse(Long id, String nickname) {
    }
}
