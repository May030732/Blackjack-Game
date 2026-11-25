package blackjack;

import blackjack.model.BlackJackGame;
import blackjack.model.Card;
import blackjack.model.GameRecord;
import blackjack.model.GameState;
import blackjack.model.Hand;
import blackjack.model.Rank;
import blackjack.model.Suit;
import blackjack.repository.HistoryRepository;
import blackjack.repository.MemberRepository;

import java.io.Console;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.springframework.dao.DataAccessException;

/** 終端機版 Blackjack，由 Spring Boot 提供 SQLite 連線。 */
public class ConsoleGame {

    private final BlackJackGame game = new BlackJackGame();
    private final Scanner scanner = new Scanner(System.in);
    private final Console console = System.console();

    private final MemberRepository members;
    private final HistoryRepository history;

    ConsoleGame(MemberRepository members, HistoryRepository history) {
        this.members = members;
        this.history = history;
    }

    public static void main(String[] args) {
        BlackjackApplication.runConsole(args);
    }

    /** 輸入結束時跳出遊戲。 */
    private static class InputClosedException extends RuntimeException {
    }

    // 讓同一個 package 的 BlackjackApplication 可以呼叫。
    void run() {
        System.out.println("=== Blackjack ===");

        try {
            // 訪客會得到 null，會員會得到自己的 uid。
            Integer memberUid = chooseMode();
            System.out.println("初始籌碼：" + game.getPlayer().getChips());

            while (game.getState() != GameState.GAME_OVER) {
                int bet = askBet();
                if (bet == 0) {
                    break;
                }

                // startRound() 會扣下注金額，所以要先記下下注前的籌碼。
                int chipsBeforeBet = game.getPlayer().getChips();
                if (!game.startRound(bet)) {
                    System.out.println("下注失敗，請重新輸入。");
                    continue;
                }

                // 若發牌時已直接結算，playerTurn() 會略過要牌提示。
                playerTurn();
                showResult();

                if (memberUid != null) {
                    saveRound(memberUid, bet, chipsBeforeBet);
                }
            }
        } catch (InputClosedException e) {
            System.out.println();
        }

        System.out.println("遊戲結束。最終籌碼：" + game.getPlayer().getChips());
    }

    private Integer chooseMode() {
        while (true) {
            System.out.println("\n1. 訪客遊玩");
            System.out.println("2. 會員登入");
            System.out.println("3. 建立帳號");
            System.out.print("請選擇：");

            String choice = readLine();

            if (choice.equals("1")) {
                System.out.println("已進入訪客模式。");
                return null;
            }

            if (!choice.equals("2") && !choice.equals("3")) {
                System.out.println("請輸入 1、2 或 3。");
                continue;
            }

            System.out.print("名字：");
            String name = readLine();

            if (name.isBlank() || name.length() > 50) {
                System.out.println("名字請輸入 1 至 50 個字元。");
                continue;
            }

            char[] password = readPassword();
            try {
                if (password.length < 8 || password.length > 128) {
                    System.out.println("密碼請輸入 8 至 128 個字元。");
                    continue;
                }

                if (choice.equals("3")) {
                    int uid = members.register(name, password);
                    System.out.println("帳號建立成功，歡迎 " + name + "！");
                    return uid;
                }

                Integer uid = members.authenticate(name, password);
                if (uid != null) {
                    System.out.println("登入成功，歡迎 " + name + "！");
                    return uid;
                }

                System.out.println("名字或密碼不正確。還沒有帳號可以選 3 建立。");
            } catch (MemberRepository.NameTakenException e) {
                System.out.println("這個名字已被註冊，請登入或換一個名字。");
            } catch (DataAccessException e) {
                System.out.println("目前無法讀寫會員資料。");
            } finally {
                Arrays.fill(password, '\0');
            }
        }
    }

    private void saveRound(int uid, int bet, int chipsBeforeBet) {
        GameRecord record = new GameRecord();
        record.setUid(uid);
        record.setBetAmount(bet);
        record.setPlayerScore(game.getPlayer().getHand().calculateScore());
        record.setDealerScore(game.getDealer().getHand().calculateScore());
        record.setResult(game.getResult().name());

        // 用結算後減去下注前的籌碼，得到這一局的淨變動。
        record.setChipChange(game.getPlayer().getChips() - chipsBeforeBet);

        try {
            history.save(record);
            System.out.println("本局紀錄已儲存。");
        } catch (DataAccessException e) {
            System.out.println("本局紀錄儲存失敗。");
        }
    }

    private char[] readPassword() {
        if (console != null) {
            char[] password = console.readPassword("密碼：");
            if (password == null) {
                throw new InputClosedException();
            }
            return password;
        }

        System.out.print("密碼（此終端會顯示輸入）：");
        try {
            return scanner.nextLine().toCharArray();
        } catch (NoSuchElementException e) {
            throw new InputClosedException();
        }
    }

    private String readLine() {
        if (console != null) {
            String line = console.readLine();
            if (line == null) {
                throw new InputClosedException();
            }
            return line.trim();
        }

        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            throw new InputClosedException();
        }
    }

    /** 詢問下注金額，輸入 0 代表離開。 */
    private int askBet() {
        while (true) {
            int chips = game.getPlayer().getChips();
            System.out.print("\n目前籌碼 " + chips
                    + "，請輸入下注金額（1~" + chips + "，0 離開）：");

            int bet;
            try {
                bet = Integer.parseInt(readLine());
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

    private void printTable(boolean revealDealer) {
        Hand dealerHand = game.getDealer().getHand();
        Hand playerHand = game.getPlayer().getHand();

        System.out.println("\n------------------------------");

        List<Card> dealerCards = dealerHand.getCards();
        StringBuilder cards = new StringBuilder();
        for (int i = 0; i < dealerCards.size(); i++) {
            if (i > 0) {
                cards.append(' ');
            }
            if (revealDealer || game.getDealer().isCardVisible(i)) {
                cards.append(formatCard(dealerCards.get(i)));
            } else {
                cards.append("[??]");
            }
        }

        String dealerScore = revealDealer
                ? String.valueOf(dealerHand.calculateScore())
                : "?";
        System.out.println("莊家：" + cards + "  (點數 " + dealerScore + ")");

        cards.setLength(0);
        for (Card card : playerHand.getCards()) {
            if (cards.length() > 0) {
                cards.append(' ');
            }
            cards.append(formatCard(card));
        }
        System.out.println("玩家：" + cards + "  (點數 " + playerHand.calculateScore() + ")");
        System.out.println("------------------------------");
    }

    private void showResult() {
        printTable(true);

        if (game.getResult() != null) {
            switch (game.getResult()) {
                case PLAYER_WIN -> System.out.println(
                        game.getPlayer().getHand().isBlackjack()
                                ? "Blackjack！玩家獲勝！" : "玩家獲勝！");
                case DEALER_WIN -> System.out.println(
                        game.getPlayer().getHand().isBust()
                                ? "爆牌，莊家獲勝。" : "莊家獲勝。");
                case PUSH -> System.out.println("平手，退回賭注。");
            }
        }

        System.out.println("目前籌碼：" + game.getPlayer().getChips());

        if (game.getState() == GameState.GAME_OVER) {
            System.out.println("玩家已經破產了！");
        }
    }

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