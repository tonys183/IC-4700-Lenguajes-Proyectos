package ecr.ac.tec.lenguajes.pokerServer.player.dealer;

import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.List;

public interface PokerDealer {
    void deal(List<PokerPlayer> players);
    void reset();
}
