package blackjack.controller;

import blackjack.model.BlackJackGame;
import blackjack.model.Card;
import blackjack.model.Dealer;
import blackjack.model.GameRecord;
import blackjack.model.GameState;
import blackjack.repository.HistoryRepository;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final HistoryRepository historyRepository;

    public GameController(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @GetMapping("/state")
    public GameView getState(HttpSession session) {
        synchronized (session) {
            return toView(getGame(session));
        }
    }

    @PostMapping("/action")
    public ResponseEntity<?> act(
            @RequestBody GameAction request, HttpSession session) {

        synchronized (session) {
            BlackJackGame game = getGame(session);

            if (request.action() == null) {
                return badRequest("請選擇遊戲操作。");
            }

            try {
                // 上一局若尚未存檔，先補存，再處理新操作。
                saveSettledRound(game, session);

                switch (request.action()) {
                    case "bet" -> {
                        int chipsBeforeBet = game.getPlayer().getChips();

                        if (request.bet() == null || !game.startRound(request.bet())) {
                            return badRequest("目前無法下注，請確認牌局狀態和下注金額。");
                        }

                        // 下注時記下本局所屬的會員與下注前籌碼。
                        if (session.getAttribute("memberUid") instanceof Integer uid) {
                            session.setAttribute(
                                    "roundHistory",
                                    new RoundHistory(uid, chipsBeforeBet));
                        }
                    }
                    case "hit" -> {
                        if (game.getState() != GameState.PLAYER_TURN) {
                            return badRequest("目前還不能要牌。");
                        }

                        game.hit();
                    }
                    case "stand" -> {
                        if (game.getState() != GameState.PLAYER_TURN) {
                            return badRequest("目前還不能停牌。");
                        }

                        game.stand();
                    }
                    default -> {
                        return badRequest("不支援這個遊戲操作。");
                    }
                }

                // 停牌、爆牌或開局 Blackjack 都可能在這裡完成結算。
                saveSettledRound(game, session);
                return ResponseEntity.ok(toView(game));
            } catch (DataAccessException e) {
                return ResponseEntity.internalServerError().body(Map.of(
                        "error",
                        "對局紀錄暫時無法儲存。請重新取得牌局狀態，下一次操作時會再試。"));
            }
        }
    }

    private void saveSettledRound(BlackJackGame game, HttpSession session) {
        GameState state = game.getState();

        if (state != GameState.ROUND_OVER && state != GameState.GAME_OVER) {
            return;
        }

        if (!(session.getAttribute("roundHistory") instanceof RoundHistory round)) {
            return;
        }

        GameRecord record = new GameRecord();
        record.setUid(round.uid());
        record.setBetAmount(game.getCurrentBet());
        record.setPlayerScore(game.getPlayer().getHand().calculateScore());
        record.setDealerScore(game.getDealer().getHand().calculateScore());
        record.setResult(game.getResult().name());
        record.setChipChange(game.getPlayer().getChips() - round.chipsBeforeBet());

        historyRepository.save(record);

        // 存檔成功後移除本局資料，避免下一次操作重複寫入。
        session.removeAttribute("roundHistory");
    }

    private BlackJackGame getGame(HttpSession session) {
        BlackJackGame game = (BlackJackGame) session.getAttribute("blackjackGame");

        if (game == null) {
            game = new BlackJackGame();
            session.setAttribute("blackjackGame", game);
        }

        return game;
    }

    private GameView toView(BlackJackGame game) {
        GameState state = game.getState();

        String phase = switch (state) {
            case READY -> "betting";
            case PLAYER_TURN -> "player-turn";
            case DEALER_TURN -> "dealer-turn";
            case ROUND_OVER -> "settled";
            case GAME_OVER -> "game-over";
        };

        List<String> actions = switch (state) {
            case READY, ROUND_OVER -> List.of("bet");
            case PLAYER_TURN -> List.of("hit", "stand");
            case DEALER_TURN, GAME_OVER -> List.of();
        };

        String result = game.getResult() == null ? null : game.getResult().name();
        int playerScore = game.getPlayer().getHand().calculateScore();

        List<CardView> playerCards = game.getPlayer().getHand().getCards()
                .stream()
                .map(this::toCardView)
                .toList();

        Dealer dealer = game.getDealer();
        List<Card> dealerHand = dealer.getHand().getCards();
        List<CardView> dealerCards = new ArrayList<>();

        for (int i = 0; i < dealerHand.size(); i++) {
            if (dealer.isCardVisible(i)) {
                dealerCards.add(toCardView(dealerHand.get(i)));
            } else {
                // 暗牌只回傳牌背所需的標記。
                dealerCards.add(new CardView(null, null, true));
            }
        }

        return new GameView(
                phase,
                actions,
                playerScore,
                result,
                playerCards,
                dealerCards,
                game.getCurrentBet(),
                game.getPlayer().getChips());
    }

    private CardView toCardView(Card card) {
        return new CardView(
                card.getRank().name(),
                card.getSuit().name(),
                false);
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    private record RoundHistory(int uid, int chipsBeforeBet) {}

    public record GameAction(String action, Integer bet) {}

    public record CardView(String rank, String suit, boolean hidden) {}

    public record GameView(
            String phase,
            List<String> availableActions,
            int playerScore,
            String result,
            List<CardView> playerCards,
            List<CardView> dealerCards,
            int currentBet,
            int chips) {}
}