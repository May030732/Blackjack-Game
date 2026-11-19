
package blackjack.model;

// 一筆 Blackjack 遊戲歷史紀錄
public class GameRecord {

    private int id;
    private int betAmount;
    private int playerScore;
    private int dealerScore;
    private String result;
    private int chipChange;
    private String playedAt;

    // 建立新的遊戲紀錄時使用
    public GameRecord(int betAmount, int playerScore,
                      int dealerScore, String result,
                      int chipChange) {
        this.betAmount = betAmount;
        this.playerScore = playerScore;
        this.dealerScore = dealerScore;
        this.result = result;
        this.chipChange = chipChange;
    }

    // 讀取資料庫紀錄時使用
    public GameRecord(int id, int betAmount,
                      int playerScore, int dealerScore,
                      String result, int chipChange,
                      String playedAt) {
        this(betAmount, playerScore, dealerScore,
             result, chipChange);
        this.id = id;
        this.playedAt = playedAt;
    }

    public int getId() {
        return id;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getDealerScore() {
        return dealerScore;
    }

    public String getResult() {
        return result;
    }

    public int getChipChange() {
        return chipChange;
    }

    public String getPlayedAt() {
        return playedAt;
    }
}

