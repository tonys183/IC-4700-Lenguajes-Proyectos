package ecr.ac.tec.lenguajes.pokerServer.hand;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import java.util.ArrayList;
import java.util.List;

public final class DownHandRevealer implements HandRevealer {
    private final List<Card> hand;

    public DownHandRevealer() {
        this.hand = new ArrayList<>();
    }

    @Override
    public void receiveCard(Card card) {
        hand.add(card);
    }

    @Override
    public List<Card> show() {
        return this.hand;
    }
}
