
package blackjack.controller;

import blackjack.model.PlayerAccount;
import blackjack.repository.PlayerRepository;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 玩家資料 API
@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerRepository playerRepository;

    // Spring Boot 自動注入 Repository
    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // 新增玩家
    // POST http://localhost:8080/api/players
    @PostMapping
    public ResponseEntity<?> createPlayer(
            @RequestBody Map<String, String> request) {

        String playerName = request.get("playerName");

        // 檢查玩家姓名是否為空
        if (playerName == null || playerName.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                        "error", "Player name cannot be empty"
                    ));
        }

        // 去除姓名前後的空白
        playerName = playerName.trim();

        // 新增到 SQLite
        int rows = playerRepository.save(playerName);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "Player created successfully",
                    "playerName", playerName,
                    "rowsAffected", rows
                ));
    }

    // 查詢所有玩家（新增）
    // GET http://localhost:8080/api/players
    @GetMapping
    public List<PlayerAccount> getAllPlayers() {

        // 呼叫 Repository，取得 SQLite 所有玩家
        return playerRepository.findAll();
    }
}
