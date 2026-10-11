package ecr.ac.tec.lenguajes.pokerGUI.model.card;

import java.util.ArrayList;
import java.util.List;

public final class ClientHand implements HandRevealer {
    private final List<Card> cards = new ArrayList<>();

    @Override
    public List<Card> show() {
        return new ArrayList<>(cards);
    }

    @Override
    public void receiveCard(Card card) {
        cards.add(card);
    }

    public void clear() {
        cards.clear();
    }
}
