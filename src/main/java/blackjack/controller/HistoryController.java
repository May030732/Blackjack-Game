
package blackjack.controller;

import blackjack.model.GameRecord;
import blackjack.repository.HistoryRepository;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 歷史紀錄 API
@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryRepository historyRepository;

    // Spring Boot 自動注入 Repository
    public HistoryController(
            HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    // ==========================================
    // 功能 1：查詢全部歷史紀錄
    // GET http://localhost:8080/api/history
    // ==========================================
    @GetMapping
    public List<GameRecord> getAllHistory() {
        return historyRepository.findAll();
    }

    // ==========================================
    // 功能 2：依照紀錄 ID 查詢
    // GET http://localhost:8080/api/history/1
    // ==========================================
    @GetMapping("/{id}")
    public ResponseEntity<GameRecord> getHistoryById(
            @PathVariable int id) {

        GameRecord record = historyRepository.findById(id);

        if (record == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(record);
    }

    // ==========================================
    // 功能 3：依照玩家姓名查詢
    // GET /api/history/search?name=Amy
    // ==========================================
    @GetMapping("/search")
    public List<GameRecord> searchHistoryByName(
            @RequestParam String name) {

        return historyRepository.findByPlayerName(name);
    }

    // ==========================================
    // 功能 4：依照玩家 UID 查詢
    // GET /api/history/player/1
    // ==========================================
    @GetMapping("/player/{uid}")
    public List<GameRecord> searchHistoryByUid(
            @PathVariable int uid) {

        return historyRepository.findByUid(uid);
    }

    // ==========================================
    // 功能 5：新增遊戲歷史紀錄
    // POST http://localhost:8080/api/history
    // ==========================================
    @PostMapping
    public ResponseEntity<String> createHistory(
            @RequestBody GameRecord record) {

        // 將收到的遊戲紀錄交給 Repository
        int rows = historyRepository.save(record);

        // 成功新增一筆資料
        if (rows == 1) {
            return ResponseEntity
                    .status(201)
                    .body("History saved successfully!");
        }

        // 新增失敗
        return ResponseEntity
                .internalServerError()
                .body("History save failed!");
    }
}
