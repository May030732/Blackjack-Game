package blackjack.model;

public class Player {

    private final String playerName;
    private final Hand hand;
    private int chips;

    public Player(int initialChips) {
        this("Guest", initialChips);
    }

    public Player(String playerName, int initialChips) {
        if (initialChips < 0) {
            throw new IllegalArgumentException("Initial chips cannot be negative.");
        }
        this.playerName = (playerName == null || playerName.isBlank()) ? "Guest" : playerName;
        this.hand = new Hand();
        this.chips = initialChips;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Hand getHand() {
        return hand;
    }

    public int getChips() {
        return chips;
    }

    public boolean placeBet(int amount) {
        if (amount <= 0 || amount > chips) {
            return false;
        }
        chips -= amount;
        return true;
    }

    public void receivePayout(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payout amount cannot be negative.");
        }
        chips += amount;
    }

    public boolean isBankrupt() {
        return chips <= 0;
    }
}