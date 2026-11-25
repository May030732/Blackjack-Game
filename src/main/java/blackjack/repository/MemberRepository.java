package blackjack.repository;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbc;
    private final SecureRandom random = new SecureRandom();

    public MemberRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 新增玩家和會員帳號。兩筆資料要一起成功。
    @Transactional
    public int register(String name, char[] password) {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] passwordHash = hash(password, salt);

        // 建立玩家，取得 SQLite 剛產生的 uid。
        int uid = jdbc.queryForObject(
                "INSERT INTO players (player_name) VALUES (?) RETURNING uid",
                Integer.class,
                name
        );

        // 用同一個 uid 建立會員帳號。
        int inserted = jdbc.update("""
                INSERT INTO members (uid, login_name, password_salt, password_hash)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (login_name) DO NOTHING
                """,
                uid, name, salt, passwordHash
        );

        // 名字重複時拋出例外，讓 Spring 一併撤回剛才新增的玩家。
        if (inserted == 0) {
            throw new NameTakenException();
        }

        return uid;
    }

    // 登入成功回傳會員 uid，名字或密碼錯誤則回傳 null。
    public Integer authenticate(String name, char[] password) {
        var matches = jdbc.query("""
                SELECT uid, password_salt, password_hash
                FROM members
                WHERE login_name = ?
                """,
                (rs, row) -> new Credentials(
                        rs.getInt("uid"),
                        rs.getBytes("password_salt"),
                        rs.getBytes("password_hash")
                ),
                name
        );

        if (matches.isEmpty()) {
            return null;
        }

        Credentials member = matches.getFirst();
        byte[] enteredPasswordHash = hash(password, member.salt());

        return MessageDigest.isEqual(member.hash(), enteredPasswordHash)
                ? member.uid()
                : null;
    }

    // 使用密碼和鹽值計算雜湊，SQLite 只存鹽值與雜湊。
    private static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 600_000, 256);

        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("無法處理會員密碼。", e);
        } finally {
            spec.clearPassword();
        }
    }

    private record Credentials(int uid, byte[] salt, byte[] hash) {}

    public static class NameTakenException extends RuntimeException {}
}