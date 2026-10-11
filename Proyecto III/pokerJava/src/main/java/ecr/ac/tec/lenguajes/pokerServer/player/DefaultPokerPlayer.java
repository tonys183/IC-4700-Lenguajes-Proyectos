package ecr.ac.tec.lenguajes.pokerServer.player;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.hand.HandRevealer;
import java.util.ArrayList;
import java.util.List;

public final class DefaultPokerPlayer implements PokerPlayer {
    private HandRevealer hand;
    private boolean active;
    private boolean bigBlind;
    private boolean smallBlind;
    private double totalAmount;
    private boolean dealer;
    private boolean turn;
    private final List<Card> communityCards;

    public DefaultPokerPlayer() {
        this.active = true;
        this.bigBlind = false;
        this.smallBlind = false;
        this.totalAmount = 0;
        this.dealer = false;
        this.turn = false;
        this.communityCards = new ArrayList<>();
    }

    @Override
    public void setDealer(Boolean dealerStatus) {
        this.dealer = dealerStatus;
    }

    @Override
    public Boolean isDealer() {
        return this.dealer;
    }

    @Override
    public void initialize(HandRevealer hand) {
        this.hand = hand;
        this.active = true;
        this.bigBlind = this.smallBlind = false;
        this.communityCards.clear();
    }

    @Override
    public void callOrRaise(double amount) {
        this.totalAmount -= amount;
    }

    @Override
    public void fold() {
        this.active = false;
    }

    @Override
    public void check() {
        this.totalAmount += 0;
    }

    @Override
    public void addAmount(double amount) {
        this.totalAmount += amount;
    }

    @Override
    public HandRevealer show() {
        return this.hand;
    }

    @Override
    public void receiveCard(Card card) {
        this.hand.receiveCard(card);
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(final boolean status) {
        this.active = status;
    }

    @Override
    public boolean isBigBlind() {
        return this.bigBlind;
    }

    @Override
    public boolean isSmallBlind() {
        return this.smallBlind;
    }

    @Override
    public void setBigBlind(boolean status) {
        this.bigBlind = status;
    }

    @Override
    public void setSmallBlind(boolean status) {
        this.smallBlind = status;
    }

    @Override
    public double getTotalAmount() {
        return this.totalAmount;
    }

    @Override
    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    @Override
    public boolean isTurn() {
        return this.turn;
    }

    @Override
    public void receiveCommunityCard(Card card) {
        communityCards.add(card);
    }

    @Override
    public List<Card> getCommunityCards() {
        return this.communityCards;
    }
}
