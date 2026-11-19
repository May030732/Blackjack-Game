
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

    // 查詢全部歷史紀錄
    // GET http://localhost:8080/api/history
    @GetMapping
    public List<GameRecord> getAllHistory() {
        return historyRepository.findAll();
    }

    // 查詢指定編號的歷史紀錄
    // GET http://localhost:8080/api/history/1
    @GetMapping("/{id}")
    public ResponseEntity<GameRecord> getHistoryById(
            @PathVariable int id) {

        GameRecord record = historyRepository.findById(id);

        if (record == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(record);
    }
}

