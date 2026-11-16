package com.gamsang.note.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 자체 가입·로그인. 로그인 상태는 서버 세션에 회원 번호를 넣어 기억한다.
 * 브라우저는 세션 쿠키(JSESSIONID, HttpOnly·SameSite=Lax)만 갖고 있어서 자바스크립트로 꺼내거나 다른 사이트에서 보낼 수 없다.
 * ponytail: 로그인 시도 횟수 제한이 없다. 공개 운영이면 IP·계정별 제한을 붙여야 한다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String BAD_LOGIN = "이메일 또는 비밀번호가 맞지 않습니다.";

    private final UserRepository userRepository;
    // BCrypt: 비밀번호마다 다른 소금(salt)을 섞고 일부러 느리게 계산해서, DB가 새도 원래 비밀번호를 알아내기 어렵다.
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record SignupRequest(String email, String nickname, String password) {
    }

    public record LoginRequest(String email, String password) {
    }

    public record Me(Long id, String nickname, String email) {
        static Me from(User u) {
            return new Me(u.getId(), u.getNickname(), u.getEmail());
        }
    }

    // POST /api/auth/signup  {email, nickname, password}  → 가입하고 바로 로그인
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Me signup(@RequestBody SignupRequest req, HttpServletRequest http) {
        String email = normalize(req.email());
        String nickname = req.nickname() == null ? "" : req.nickname().trim();
        String password = req.password() == null ? "" : req.password();
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+") || email.length() > 255) {
            throw bad("이메일 형식이 올바르지 않습니다.");
        }
        if (nickname.isEmpty() || nickname.length() > 30) throw bad("닉네임은 1~30자로 입력해 주세요.");
        // BCrypt 는 72바이트까지만 쓰므로 길이를 제한한다.
        if (password.length() < 8 || password.length() > 64) throw bad("비밀번호는 8~64자로 입력해 주세요.");
        // 같은 이메일로 동시에 가입하는 드문 경우는 DB의 uq_users_email 이 막는다(그땐 500).
        if (userRepository.existsByEmail(email)) throw conflict();

        User user = userRepository.save(new User(nickname, email, passwordEncoder.encode(password)));
        startSession(http, user.getId());
        return Me.from(user);
    }

    // POST /api/auth/login  {email, password}
    @PostMapping("/login")
    @Transactional(readOnly = true)
    public Me login(@RequestBody LoginRequest req, HttpServletRequest http) {
        User user = userRepository.findByEmail(normalize(req.email()))
                .filter(u -> req.password() != null && passwordEncoder.matches(req.password(), u.getPasswordHash()))
                // 이메일이 없는지, 비밀번호가 틀렸는지 구분해 알려 주지 않는다(가입된 이메일 추측 방지).
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, BAD_LOGIN));
        startSession(http, user.getId());
        return Me.from(user);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest http) {
        HttpSession session = http.getSession(false);
        if (session != null) session.invalidate();
    }

    // GET /api/auth/me  → 로그인한 회원, 아니면 401
    @GetMapping("/me")
    @Transactional(readOnly = true)
    public Me me(@LoginUser Long me) {
        return userRepository.findById(me).map(Me::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));
    }

    // 로그인 전 세션을 버리고 새 세션을 만든다(세션 고정 공격 방지: 로그인 전에 심어 둔 세션 ID를 계속 쓰지 못하게).
    private static void startSession(HttpServletRequest http, Long userId) {
        HttpSession old = http.getSession(false);
        if (old != null) old.invalidate();
        http.getSession(true).setAttribute(LoginUserResolver.SESSION_KEY, userId);
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private static ResponseStatusException conflict() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
    }
}
