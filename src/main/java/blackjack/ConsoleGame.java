package blackjack;

import blackjack.model.BlackJackGame;
import blackjack.model.Card;
import blackjack.model.GameState;
import blackjack.model.Hand;
import blackjack.model.Rank;
import blackjack.model.Suit;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Console 版 Blackjack（純文字，UI 之後再說）
 * 執行方式：不經過 Spring，直接跑這個 main。
 */
public class ConsoleGame {

    private final BlackJackGame game = new BlackJackGame();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new ConsoleGame().run();
    }

    /** 輸入串流結束（Ctrl+D / 管線讀完）時用來跳出整個遊戲。 */
    private static class InputClosedException extends RuntimeException {
    }

    // ===== 主迴圈 =====
    private void run() {
        System.out.println("=== Blackjack ===");
        System.out.println("初始籌碼：" + game.getPlayer().getChips());

        try {
            while (game.getState() != GameState.GAME_OVER) {

                int bet = askBet();
                if (bet == 0) {          // 約定：輸入 0 代表離開
                    break;
                }

                if (!game.startRound(bet)) {
                    System.out.println("下注失敗，請重新輸入。");
                    continue;
                }

                // 開局就 Blackjack 時 state 已是 ROUND_OVER，playerTurn 會直接略過
                playerTurn();
                showResult();
            }
        } catch (InputClosedException e) {
            System.out.println();
        }

        System.out.println("遊戲結束。最終籌碼：" + game.getPlayer().getChips());
    }

    // ===== 輸入 =====
    /** 詢問下注金額；回傳 0 代表離開。要處理非數字輸入。 */
    private int askBet() {
        while (true) {
            int chips = game.getPlayer().getChips();
            System.out.print("\n目前籌碼 " + chips + "，請輸入下注金額（1~" + chips + "，0 離開）：");
            String line = readLine();

            int bet;
            try {
                bet = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("請輸入整數。");
                continue;
            }

            if (bet == 0) {
                return 0;
            }
            if (bet < 0) {
                System.out.println("下注金額必須大於 0。");
                continue;
            }
            if (bet > chips) {
                System.out.println("籌碼不足。");
                continue;
            }
            return bet;
        }
    }

    /** 詢問 h(hit) / s(stand)；回傳小寫字元。 */
    private char askAction() {
        while (true) {
            System.out.print("要牌 (h) 還是停牌 (s)？");
            String line = readLine().toLowerCase();
            if (line.equals("h") || line.equals("s")) {
                return line.charAt(0);
            }
            System.out.println("請輸入 h 或 s。");
        }
    }

    private String readLine() {
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            throw new InputClosedException();
        }
    }

    // ===== 玩家回合 =====
    private void playerTurn() {
        while (game.getState() == GameState.PLAYER_TURN) {
            printTable(false);
            if (askAction() == 'h') {
                game.hit();
            } else {
                game.stand();
            }
        }
    }

    // ===== 顯示 =====
    /**
     * 印出牌桌。
     * @param revealDealer false 時，莊家第二張牌要蓋住（用 dealer.isCardVisible(i)）
     */
    private void printTable(boolean revealDealer) {
        Hand dealerHand = game.getDealer().getHand();
        Hand playerHand = game.getPlayer().getHand();

        System.out.println("\n------------------------------");

        List<Card> dealerCards = dealerHand.getCards();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dealerCards.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            if (revealDealer || game.getDealer().isCardVisible(i)) {
                sb.append(formatCard(dealerCards.get(i)));
            } else {
                sb.append("[??]");
            }
        }
        String dealerScore = revealDealer
                ? String.valueOf(dealerHand.calculateScore())
                : "?";
        System.out.println("莊家：" + sb + "  (點數 " + dealerScore + ")");

        sb.setLength(0);
        for (Card card : playerHand.getCards()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(formatCard(card));
        }
        System.out.println("玩家：" + sb + "  (點數 " + playerHand.calculateScore() + ")");
        System.out.println("------------------------------");
    }

    private void showResult() {
        printTable(true);

        if (game.getResult() != null) {
            switch (game.getResult()) {
                case PLAYER_WIN -> System.out.println(
                        game.getPlayer().getHand().isBlackjack() ? "Blackjack！玩家獲勝！" : "玩家獲勝！");
                case DEALER_WIN -> System.out.println(
                        game.getPlayer().getHand().isBust() ? "爆牌，莊家獲勝。" : "莊家獲勝。");
                case PUSH -> System.out.println("平手，退回賭注。");
            }
        }

        System.out.println("目前籌碼：" + game.getPlayer().getChips());

        if (game.getState() == GameState.GAME_OVER) {
            System.out.println("玩家已經破產了！");
        }
    }

    // ===== 工具 =====
    private static String formatCard(Card card) {
        return "[" + suitSymbol(card.getSuit()) + rankSymbol(card.getRank()) + "]";
    }

    private static String suitSymbol(Suit suit) {
        return switch (suit) {
            case HEARTS -> "紅心";
            case DIAMONDS -> "方塊";
            case CLUBS -> "梅花";
            case SPADES -> "黑桃";
        };
    }

    private static String rankSymbol(Rank rank) {
        return switch (rank) {
            case ACE -> "A";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            default -> String.valueOf(rank.getValue());
        };
    }
}