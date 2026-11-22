import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards;
    private int currentIndex;

    public Deck() {
        this.cards = new ArrayList<>();
        initialize();
        shuffle();
    }

    private void initialize() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        currentIndex = 0;
    }

    public void shuffle() {
        currentIndex = 0;
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot draw a card: the deck is empty.");
        }
        return cards.get(currentIndex++);
    }

    public boolean isEmpty() {
        return currentIndex >= cards.size();
    }
}