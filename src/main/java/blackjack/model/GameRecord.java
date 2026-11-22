
package blackjack.model;

public class GameRecord {

    private int id;
    private int uid;  //新增uid
    private int betAmount;
    private int playerScore;
    private int dealerScore;
    private String result;
    private int chipChange;
    private String playedAt;
/* 
    public GameRecord() {
        System.out.println("GameRecord 無參數建構子被呼叫");
    }

    public GameRecord(int id, int uid, int betAmount,
                      int playerScore, int dealerScore,
                      String result, int chipChange,
                      String playedAt) {

        this.id = id;
        this.uid = uid;
        this.betAmount = betAmount;
        this.playerScore = playerScore;
        this.dealerScore = dealerScore;
        this.result = result;
        this.chipChange = chipChange;
        this.playedAt = playedAt;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getDealerScore() {
        return dealerScore;
    }

    public void setDealerScore(int dealerScore) {
        this.dealerScore = dealerScore;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getChipChange() {
        return chipChange;
    }

    public void setChipChange(int chipChange) {
        this.chipChange = chipChange;
    }

    public String getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(String playedAt) {
        this.playedAt = playedAt;
    }
}


