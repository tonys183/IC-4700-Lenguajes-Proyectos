package ecr.ac.tec.lenguajes.pokerServer.player;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.hand.HandRevealer;
import java.util.List;

public interface PokerPlayer {
    void initialize(HandRevealer hand);
    void callOrRaise(double amount);
    void fold();
    void check();
    void addAmount(double amount);
    HandRevealer show();
    void receiveCard(Card card);
    boolean isActive();
    void setActive(boolean status);
    boolean isBigBlind();
    boolean isSmallBlind();
    void setBigBlind(boolean status);
    void setSmallBlind(boolean status);
    double getTotalAmount();
    void setDealer(Boolean dealerStatus);
    Boolean isDealer();
    void setTurn(boolean turn);
    boolean isTurn();
    void receiveCommunityCard(Card card);
    List<Card> getCommunityCards();
    default void receiveCard(Card card, boolean isVisible) { receiveCard(card); }
    default void setTurn(boolean turn, boolean canFold) { setTurn(turn); }
    default void setTurn(boolean turn, boolean canFold, double callAmount) { setTurn(turn, canFold); }
}
