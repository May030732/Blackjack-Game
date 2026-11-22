
package blackjack.model;

public class PlayerAccount {

    private int uid;
    private String playerName;

    // 建構子
    public PlayerAccount(int uid, String playerName) {
        this.uid = uid;
        this.playerName = playerName;
    }

    public int getUid() {
        return uid;
    }

    public String getPlayerName() {
        return playerName;
    }
}
