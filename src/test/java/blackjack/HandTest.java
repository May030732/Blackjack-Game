package blackjack;

public class HandTest {

    public static void main(String[] args) {

        // Test 1：A + K = Blackjack
        testHand(
            "A + K",
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.SPADES, Rank.KING)
        );

        // Test 2：A + 8 = 19
        testHand(
            "A + 8",
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.SPADES, Rank.EIGHT)
        );

        // Test 3：A + 8 + 9 = 18
        testHand(
            "A + 8 + 9",
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.SPADES, Rank.EIGHT),
            new Card(Suit.CLUBS, Rank.NINE)
        );

        // Test 4：K + Q + 5 = Bust
        testHand(
            "K + Q + 5",
            new Card(Suit.HEARTS, Rank.KING),
            new Card(Suit.SPADES, Rank.QUEEN),
            new Card(Suit.CLUBS, Rank.FIVE)
        );

        // Test 5：A + A + 9 = 21
        testHand(
            "A + A + 9",
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.SPADES, Rank.ACE),
            new Card(Suit.CLUBS, Rank.NINE)
        );

        // Test 6：10 + 7 + 4 = 21
        testHand(
            "10 + 7 + 4",
            new Card(Suit.HEARTS, Rank.TEN),
            new Card(Suit.SPADES, Rank.SEVEN),
            new Card(Suit.CLUBS, Rank.FOUR)
        );

    }

    // 共用測試方法
    public static void testHand(String testName, Card... testCards) {

        Hand hand = new Hand();

        // 將測試卡牌加入手牌
        for (Card card : testCards) {
            hand.addCard(card);
        }

        System.out.println("========================");
        System.out.println("Test: " + testName);

        System.out.println("Cards: " + hand.getCards());
        System.out.println("Score: " + hand.calculateScore());
        System.out.println("Bust: " + hand.isBust());
        System.out.println("Blackjack: " + hand.isBlackjack());

    }
}
    