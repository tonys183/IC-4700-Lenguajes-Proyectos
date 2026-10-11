package ecr.ac.tec.lenguajes.pokerServer.player;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.hand.HandRevealer;
import java.io.PrintWriter;
import java.util.List;

public class DefaultNetworkPokerPlayer implements NetworkPokerPlayer {
    private static final String RESET_CMD = "GAME:RESET";
    private static final String MONEY_PREFIX = "MONEY:";
    private static final String CALL_RAISE_PREFIX = "STATUS:CALL_RAISE:";
    private static final String FOLD_CMD = "STATUS:FOLD";
    private static final String CHECK_CMD = "STATUS:CHECK";
    private static final String CARD_PREFIX = "CARD:";
    private static final String COMMUNITY_PREFIX = "COMMUNITY:";
    private static final String SEPARATOR = ":";
    private static final String ROLE_BIG_BLIND = "ROLE:BIG_BLIND";
    private static final String ROLE_SMALL_BLIND = "ROLE:SMALL_BLIND";
    private static final String ROLE_DEALER = "ROLE:DEALER";
    private static final String TURN_YOUR_TURN = "TURN:YOUR_TURN";
    private static final String TURN_WAIT = "TURN:WAIT";

    private final PokerPlayer internalPlayer;
    private final PrintWriter out;
    private final String name;

    public DefaultNetworkPokerPlayer(String name, double initialMoney, PrintWriter out) {
        this.internalPlayer = new DefaultPokerPlayer();
        this.internalPlayer.addAmount(initialMoney);
        this.out = out;
        this.name = name;
    }

    @Override
    public void initialize(HandRevealer hand) {
        internalPlayer.initialize(hand);
        out.println(RESET_CMD);
    }

    @Override
    public void callOrRaise(double amount) {
        internalPlayer.callOrRaise(amount);
        out.println(MONEY_PREFIX + internalPlayer.getTotalAmount());
        out.println(CALL_RAISE_PREFIX + amount);
    }

    @Override
    public void fold() {
        internalPlayer.fold();
        out.println(FOLD_CMD);
    }

    @Override
    public void check() {
        internalPlayer.check();
        out.println(CHECK_CMD);
    }

    @Override
    public void addAmount(double amount) {
        internalPlayer.addAmount(amount);
        out.println(MONEY_PREFIX + internalPlayer.getTotalAmount());
    }

    @Override
    public HandRevealer show() {
        return internalPlayer.show();
    }

    @Override
    public void receiveCard(Card card) {
        receiveCard(card, false);
    }

    @Override
    public void receiveCard(Card card, boolean isVisible) {
        internalPlayer.receiveCard(card);
        out.println(CARD_PREFIX + card.rank() + SEPARATOR + card.suit() + SEPARATOR + isVisible);
    }

    @Override
    public void receiveCommunityCard(Card card) {
        internalPlayer.receiveCommunityCard(card);
        out.println(COMMUNITY_PREFIX + card.rank() + SEPARATOR + card.suit());
    }

    @Override
    public List<Card> getCommunityCards() {
        return this.internalPlayer.getCommunityCards();
    }

    @Override
    public boolean isActive() {
        return internalPlayer.isActive();
    }

    @Override
    public void setActive(final boolean status) {

    }

    @Override
    public boolean isBigBlind() {
        return internalPlayer.isBigBlind();
    }

    @Override
    public boolean isSmallBlind() {
        return internalPlayer.isSmallBlind();
    }

    @Override
    public void setBigBlind(boolean status) {
        internalPlayer.setBigBlind(status);
        if (status)
            out.println(ROLE_BIG_BLIND);
    }

    @Override
    public void setSmallBlind(boolean status) {
        internalPlayer.setSmallBlind(status);
        if (status)
            out.println(ROLE_SMALL_BLIND);
    }

    @Override
    public double getTotalAmount() {
        return internalPlayer.getTotalAmount();
    }

    @Override
    public void setDealer(Boolean dealerStatus) {
        internalPlayer.setDealer(dealerStatus);
        if (dealerStatus)
            out.println(ROLE_DEALER);
    }

    @Override
    public Boolean isDealer() {
        return internalPlayer.isDealer();
    }

    @Override
    public void setTurn(boolean turn) {
        setTurn(turn, true);
    }

    @Override
    public void setTurn(boolean turn, boolean canFold) {
        setTurn(turn, canFold, 0.0);
    }

    @Override
    public void setTurn(boolean turn, boolean canFold, double callAmount) {
        internalPlayer.setTurn(turn);
        if (turn) {
            out.println(TURN_YOUR_TURN + SEPARATOR + canFold + SEPARATOR + callAmount);
        } else {
            out.println(TURN_WAIT);
        }
    }

    @Override
    public boolean isTurn() {
        return internalPlayer.isTurn();
    }

    @Override
    public String getPlayerName() {
        return this.name;
    }

    @Override
    public void sendMessage(String message) {
        out.println(message);
    }
}
