
package blackjack.controller;

import blackjack.model.GameRecord;
import blackjack.repository.HistoryRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoryTestController {

    private final HistoryRepository historyRepository;

    public HistoryTestController(
            HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @GetMapping("/api/test/save-history")
    public String testSaveHistory() {

        GameRecord record = new GameRecord(
            100,           // 下注金額
            20,            // 玩家點數
            18,            // 莊家點數
            "PLAYER_WIN",  // 玩家勝利
            100            // 籌碼淨增加
        );

        int rows = historyRepository.save(record);

        return rows == 1
                ? "History saved successfully!"
                : "History save failed!";
    }
}


