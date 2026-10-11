package ecr.ac.tec.lenguajes.pokerGUI.model.player;

import ecr.ac.tec.lenguajes.pokerGUI.model.card.HandRevealer;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Card;

public interface PokerPlayer {
    void initialize(HandRevealer hand);
    void callOrRaise(double amount);
    void fold();
    void check();
    void addAmount(double amount);
    HandRevealer show();
    void receiveCard(Card card);
    boolean isActive();
    boolean isBigBlind();
    boolean isSmallBlind();
    void setBigBlind(boolean status);
    void setSmallBlind(boolean status);
    double getTotalAmount();
    void setDealer(Boolean dealerStatus);
    Boolean isDealer();
}
