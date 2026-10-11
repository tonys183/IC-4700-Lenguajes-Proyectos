package ecr.ac.tec.lenguajes.pokerGUI.model.player;

import ecr.ac.tec.lenguajes.pokerGUI.model.card.HandRevealer;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Card;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.ClientHand;

public final class DefaultPokerPlayer implements PokerPlayer {
    private final String name;
    private HandRevealer hand;
    private boolean active;
    private boolean bigBlind;
    private boolean smallBlind;
    private double totalAmount;
    private boolean dealer;

    public DefaultPokerPlayer(String name, double initialMoney) {
        this.name = name;
        this.totalAmount = initialMoney;
        this.active = true;
        this.hand = new ClientHand();
    }

    public String getName() {
        return name;
    }

    @Override
    public void initialize(HandRevealer hand) {
        this.hand = hand;
        this.active = true;
        this.bigBlind = false;
        this.smallBlind = false;
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
    public void check() { }

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
        if (this.hand != null) {
            this.hand.receiveCard(card);
        }
    }

    @Override
    public boolean isActive() {
        return this.active;
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
    public void setDealer(Boolean dealerStatus) {
        this.dealer = dealerStatus;
    }

    @Override
    public Boolean isDealer() {
        return this.dealer;
    }
}
