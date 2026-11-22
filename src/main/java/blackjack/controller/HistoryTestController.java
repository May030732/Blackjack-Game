
package blackjack.controller;

import blackjack.model.GameRecord;
import blackjack.repository.HistoryRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 測試遊戲歷史紀錄是否能成功存入 SQLite
@RestController
@RequestMapping("/api/test")
public class HistoryTestController {

    private final HistoryRepository historyRepository;

    // Spring Boot 自動注入 Repository
    public HistoryTestController(
            HistoryRepository historyRepository) {

        this.historyRepository = historyRepository;
    }

    // 測試新增一筆遊戲歷史紀錄
    // GET http://localhost:8080/api/test/save-history
    @GetMapping("/save-history")
    public String saveTestHistory() {

        // 建立空的 GameRecord 物件
        GameRecord record = new GameRecord();

        // 設定玩家 UID（測試玩家 Amy）
        record.setUid(1);

        // 設定下注金額
        record.setBetAmount(100);

        // 設定玩家點數
        record.setPlayerScore(20);

        // 設定莊家點數
        record.setDealerScore(18);

        // 設定遊戲結果
        record.setResult("PLAYER_WIN");

        // 設定籌碼變化（贏得 100）
        record.setChipChange(100);

        // 將紀錄存入 SQLite
        int rows = historyRepository.save(record);

        // 判斷是否新增成功
        if (rows == 1) {
            return "History saved successfully!";
        }

        return "History save failed!";
    }
}
