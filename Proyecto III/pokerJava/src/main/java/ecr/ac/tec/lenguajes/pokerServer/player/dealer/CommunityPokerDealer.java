package ecr.ac.tec.lenguajes.pokerServer.player.dealer;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.List;

public interface CommunityPokerDealer extends PokerDealer {
    void flop(List<Card> communityCards, List<PokerPlayer> players);
    void turn(List<Card> communityCards, List<PokerPlayer> players);
    void river(List<Card> communityCards, List<PokerPlayer> players);
}
