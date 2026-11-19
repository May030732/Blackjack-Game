
package blackjack.repository;

import blackjack.model.GameRecord;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// 告訴 Spring Boot：這個類別負責資料庫存取
@Repository
public class HistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    // Spring Boot 自動提供 JdbcTemplate
    public HistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 新增一筆遊戲歷史紀錄
    public int save(GameRecord record) {

        String sql = """
            INSERT INTO game_history
            (bet_amount, player_score, dealer_score,
             result, chip_change)
            VALUES (?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
            sql,
            record.getBetAmount(),
            record.getPlayerScore(),
            record.getDealerScore(),
            record.getResult(),
            record.getChipChange()
        );
    }

    // 查詢全部歷史紀錄，最新的排在前面
    public List<GameRecord> findAll() {

        String sql = """
            SELECT id, bet_amount, player_score,
                   dealer_score, result, chip_change,
                   played_at
            FROM game_history
            ORDER BY id DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new GameRecord(
                rs.getInt("id"),
                rs.getInt("bet_amount"),
                rs.getInt("player_score"),
                rs.getInt("dealer_score"),
                rs.getString("result"),
                rs.getInt("chip_change"),
                rs.getString("played_at")
            )
        );
    }

    // 依照紀錄編號查詢
    public GameRecord findById(int id) {

        String sql = """
            SELECT id, bet_amount, player_score,
                   dealer_score, result, chip_change,
                   played_at
            FROM game_history
            WHERE id = ?
            """;

        List<GameRecord> records =
            jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new GameRecord(
                    rs.getInt("id"),
                    rs.getInt("bet_amount"),
                    rs.getInt("player_score"),
                    rs.getInt("dealer_score"),
                    rs.getString("result"),
                    rs.getInt("chip_change"),
                    rs.getString("played_at")
                ),
                id
            );

        return records.isEmpty() ? null : records.get(0);
    }
}

