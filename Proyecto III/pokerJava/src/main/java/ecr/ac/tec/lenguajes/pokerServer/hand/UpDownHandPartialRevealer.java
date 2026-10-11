package ecr.ac.tec.lenguajes.pokerServer.hand;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import java.util.ArrayList;
import java.util.List;

public final class UpDownHandPartialRevealer implements HandPartialRevealer {
    private final List<Card> downHand;
    private final List<Card> upHand;
    private final List<Card> allHand;
    private static final int FIRST_ROUND_SIZE = 2;
    private static final int THIRD_ROUND_SIZE = 6;

    public UpDownHandPartialRevealer() {
        this.downHand = new ArrayList<>();
        this.upHand = new ArrayList<>();
        this.allHand = new ArrayList<>();
    }

    @Override
    public void receiveCard(Card card) {
        if (allHand.size() < FIRST_ROUND_SIZE) {
            downHand.add(card);
        } else if (allHand.size() == FIRST_ROUND_SIZE) {
            upHand.add(card);
        } else if (allHand.size() < THIRD_ROUND_SIZE) {
            upHand.add(card);
        } else downHand.add(card);
        allHand.add(card);
    }

    @Override
    public List<Card> showPartial() {
        return this.upHand;
    }

    @Override
    public List<Card> show() {
        return this.allHand;
    }

}
