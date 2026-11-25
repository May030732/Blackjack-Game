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

    // 共用查詢：用 UID 從 players 取出玩家姓名。
    // LEFT JOIN 會保留沒有對應玩家的舊紀錄。
    private static final String SELECT_HISTORY = """
            SELECT h.*, p.player_name
            FROM game_history h
            LEFT JOIN players p ON p.uid = h.uid
            """;

    private final JdbcTemplate jdbcTemplate;

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

    // 將每一列 SQLite 查詢結果轉成 GameRecord。
    private GameRecord mapRecord(ResultSet rs, int rowNum)
            throws SQLException {

        GameRecord record = new GameRecord();

        record.setId(rs.getInt("id"));
        record.setUid(rs.getInt("uid"));
        record.setPlayerName(rs.getString("player_name")); // 新增：填入玩家姓名
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
        String sql = SELECT_HISTORY + "ORDER BY h.id DESC";

        return jdbcTemplate.query(sql, this::mapRecord);
    }

    // 功能 3：根據紀錄 ID 查詢
    public GameRecord findById(int id) {
        String sql = SELECT_HISTORY + "WHERE h.id = ?";

        List<GameRecord> records = jdbcTemplate.query(sql, this::mapRecord, id);

        return records.isEmpty() ? null : records.get(0);
    }

    // 功能 4：根據玩家姓名查詢
    public List<GameRecord> findByPlayerName(String name) {
        String sql = SELECT_HISTORY + "WHERE p.player_name = ? ORDER BY h.id DESC";

        return jdbcTemplate.query(sql, this::mapRecord, name);
    }

    // 功能 5：根據玩家 UID 查詢
    public List<GameRecord> findByUid(int uid) {
        String sql = SELECT_HISTORY + "WHERE h.uid = ? ORDER BY h.id DESC";

        return jdbcTemplate.query(sql, this::mapRecord, uid);
    }
}