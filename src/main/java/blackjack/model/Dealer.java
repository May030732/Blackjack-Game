package blackjack.model;

public class Dealer {

    // Variables
    private Hand hand;
    private boolean holeCardRevealed;

    // Constructor
    public Dealer() {
        hand = new Hand();
        holeCardRevealed = false;
    }

    // Methods
    public Hand getHand() {
        return hand;
    }

    public void play(Deck deck) {
        revealHoleCard();
        while (hand.calculateScore() < 17 && !deck.isEmpty()) {
            hand.addCard(deck.drawCard());
        }
    }

    public void revealHoleCard() {
        holeCardRevealed = true;
    }

    public boolean isCardVisible(int index) {
        return index != 1 || holeCardRevealed;
    }

    public void reset() {
        hand.clear();
        holeCardRevealed = false;
    }
}