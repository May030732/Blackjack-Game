package blackjack.repository;

import blackjack.model.GameRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// 負責 SQLite 遊戲歷史紀錄的資料庫操作
@Repository
public class HistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    // Spring Boot 自動注入 JdbcTemplate
    public HistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 功能 1：新增遊戲歷史紀錄
    public int save(GameRecord record) {

        String sql = """
            INSERT INTO game_history
            (uid, bet_amount, player_score,
             dealer_score, result, chip_change)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
            sql,
            record.getUid(),
            record.getBetAmount(),
            record.getPlayerScore(),
            record.getDealerScore(),
            record.getResult(),
            record.getChipChange()
        );
    }

    // 共用方法：將 SQLite 查詢結果轉換成 GameRecord 物件
    private GameRecord mapRecord(ResultSet rs, int rowNum)
            throws SQLException {

        // 建立空的 GameRecord 物件
        GameRecord record = new GameRecord();

        // 將資料庫欄位設定到 Java 物件
        record.setId(rs.getInt("id"));
        record.setUid(rs.getInt("uid"));
        record.setBetAmount(rs.getInt("bet_amount"));
        record.setPlayerScore(rs.getInt("player_score"));
        record.setDealerScore(rs.getInt("dealer_score"));
        record.setResult(rs.getString("result"));
        record.setChipChange(rs.getInt("chip_change"));
        record.setPlayedAt(rs.getString("played_at"));

        return record;
    }

    // 功能 2：查詢全部歷史紀錄
    public List<GameRecord> findAll() {

        String sql = """
            SELECT *
            FROM game_history
            ORDER BY id DESC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapRecord(rs, rowNum)
        );
    }

    // 功能 3：根據紀錄 ID 查詢
    public GameRecord findById(int id) {

        String sql = """
            SELECT *
            FROM game_history
            WHERE id = ?
            """;

        List<GameRecord> records = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapRecord(rs, rowNum),
            id
        );

        // 如果找不到紀錄，回傳 null
        return records.isEmpty() ? null : records.get(0);
    }

    // 功能 4：根據玩家姓名查詢
    public List<GameRecord> findByPlayerName(String name) {

        String sql = """
            SELECT h.*
            FROM game_history h
            JOIN players p ON h.uid = p.uid
            WHERE p.player_name = ?
            ORDER BY h.id DESC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapRecord(rs, rowNum),
            name
        );
    }

    // 功能 5：根據玩家 UID 查詢
    public List<GameRecord> findByUid(int uid) {

        String sql = """
            SELECT *
            FROM game_history
            WHERE uid = ?
            ORDER BY id DESC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapRecord(rs, rowNum),
            uid
        );
    }
}
