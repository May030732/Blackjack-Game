
package blackjack.repository;

import blackjack.model.PlayerAccount;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// 告訴 Spring Boot：這個類別負責資料庫操作
@Repository
public class PlayerRepository {

    // 用來執行 SQL 指令
    private final JdbcTemplate jdbcTemplate;

    // Spring Boot 自動注入 JdbcTemplate
    public PlayerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 新增玩家（原本已經完成的功能）
    public int save(String playerName) {

        String sql = """
            INSERT INTO players (player_name)
            VALUES (?)
            """;

        return jdbcTemplate.update(sql, playerName);
    }

    // 查詢所有玩家（這次新增的功能）
    public List<PlayerAccount> findAll() {

        // 從 players 資料表查詢所有玩家
        String sql = """
            SELECT uid, player_name
            FROM players
            ORDER BY uid ASC
            """;

        // 將 SQLite 查詢結果轉換成 Java 物件
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new PlayerAccount(
                rs.getInt("uid"),
                rs.getString("player_name")
            )
        );
    }
}
