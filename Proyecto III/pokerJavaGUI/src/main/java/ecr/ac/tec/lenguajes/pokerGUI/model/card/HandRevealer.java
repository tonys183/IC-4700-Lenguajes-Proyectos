package ecr.ac.tec.lenguajes.pokerGUI.model.card;

import java.util.List;

public interface HandRevealer {
    List<Card> show();
    void receiveCard(Card cards);
}
