package ecr.ac.tec.lenguajes.pokerServer.hand;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import java.util.List;

public interface HandPartialRevealer extends HandRevealer {
    List<Card> showPartial();
}
