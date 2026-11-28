package blackjack.controller;

import blackjack.repository.MemberRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Credentials credentials) {
        if (credentials.username() == null || credentials.username().isBlank()
                || credentials.password() == null || credentials.password().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "請輸入會員姓名和密碼。"));
        }

        String username = credentials.username().trim();
        char[] password = credentials.password().toCharArray();

        try {
            int uid = memberRepository.register(username, password);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("uid", uid, "username", username));
        } catch (MemberRepository.NameTakenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "這個會員姓名已被使用。"));
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Credentials credentials,
            HttpServletRequest request) {

        if (credentials.username() == null || credentials.username().isBlank()
                || credentials.password() == null || credentials.password().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "請輸入會員姓名和密碼。"));
        }

        String username = credentials.username().trim();
        char[] password = credentials.password().toCharArray();

        Integer uid;
        try {
            uid = memberRepository.authenticate(username, password);
        } finally {
            Arrays.fill(password, '\0');
        }

        if (uid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "會員姓名或密碼錯誤。"));
        }

        // 把登入會員的 UID 保存在伺服器的 session。
        HttpSession session = request.getSession();
        request.changeSessionId();
        session.setAttribute("memberUid", uid);

        return ResponseEntity.ok(Map.of("uid", uid, "username", username));
    }

    public record Credentials(String username, String password) {}
}
