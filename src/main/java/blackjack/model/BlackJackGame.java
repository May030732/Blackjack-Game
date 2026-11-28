package blackjack.model;

public class BlackJackGame {

    private Deck deck;
    private Player player;
    private Dealer dealer;

    private int currentBet;

    private GameState state;
    private GameResult result;

    public BlackJackGame() {
        deck = new Deck();
        player = new Player(1000);
        dealer = new Dealer();

        currentBet = 0;

        state = GameState.READY;
        result = null;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public boolean startRound(int bet) {

    if (state != GameState.READY &&
        state != GameState.ROUND_OVER) {
        return false;
    }

    if (bet <= 0 || !player.placeBet(bet)) {
        return false;
    }

    
    resetRound();

    
    deck = new Deck();

    currentBet = bet;
    result = null;

    
    player.getHand().addCard(deck.drawCard());
    dealer.getHand().addCard(deck.drawCard());

    player.getHand().addCard(deck.drawCard());
    dealer.getHand().addCard(deck.drawCard());

   
    if (checkBlackjack()) {
        return true;
    }

    state = GameState.PLAYER_TURN;

    return true;
    }


    public void hit() { //玩家抽

    if (state != GameState.PLAYER_TURN) {
        return;
        }

    Card card = deck.drawCard();

    player.getHand().addCard(card);

    if (player.getHand().isBust()) {

        dealer.revealHoleCard();

        result = GameResult.DEALER_WIN;

        settleBet();
        if (state != GameState.GAME_OVER) {
            state = GameState.ROUND_OVER;
            }
        }
    }


    public void stand() { //讓步
    

    if (state != GameState.PLAYER_TURN) {
        return;
    }

    state = GameState.DEALER_TURN;

    dealerPlay();
    }


    private void dealerPlay() {

    dealer.revealHoleCard();

    dealer.play(deck);

    determineWinner();

    settleBet();
    if (state != GameState.GAME_OVER) {
        state = GameState.ROUND_OVER;
            }
    }

    private boolean checkBlackjack() {

    boolean playerBlackjack =
        player.getHand().isBlackjack();

    boolean dealerBlackjack =
        dealer.getHand().isBlackjack();

    if (!playerBlackjack && !dealerBlackjack) {
        return false;
        }

    dealer.revealHoleCard();

    determineWinner();

    settleBet();
        if (state != GameState.GAME_OVER) {
            state = GameState.ROUND_OVER;
        }

    return true;
    }

    private void determineWinner() {

    int playerScore = player.getHand().calculateScore();
    int dealerScore = dealer.getHand().calculateScore();

    if (playerScore > 21) {
        result = GameResult.DEALER_WIN;
    }
    else if (dealerScore > 21) {
        result = GameResult.PLAYER_WIN;
    }
    else if (playerScore > dealerScore) {
        result = GameResult.PLAYER_WIN;
    }
    else if (playerScore < dealerScore) {
        result = GameResult.DEALER_WIN;
    }
    else {
        result = GameResult.PUSH;
        }
    }

private void settleBet() {
        if (result == GameResult.PLAYER_WIN) {
            if (player.getHand().isBlackjack()) {
                player.receivePayout(currentBet + (currentBet * 3) / 2);
            } else {
                player.receivePayout(currentBet * 2);
            }
        } else if (result == GameResult.PUSH) {
            player.receivePayout(currentBet);
        }

        if (player.isBankrupt()) {
            state = GameState.GAME_OVER;
        }
    }

    public GameState getState() {
        return state;
    }

    public GameResult getResult() {
        return result;
    }

    public Player getPlayer() {
        return player;
    }

    public Dealer getDealer() {
        return dealer;
    }
    private void resetRound() {

    player.getHand().clear();

    dealer.reset();

    currentBet = 0;

    result = null;

    }

}