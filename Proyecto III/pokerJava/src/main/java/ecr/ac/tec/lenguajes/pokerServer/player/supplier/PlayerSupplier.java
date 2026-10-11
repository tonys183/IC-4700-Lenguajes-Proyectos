package ecr.ac.tec.lenguajes.pokerServer.player.supplier;

import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.List;

public interface PlayerSupplier {
    List<PokerPlayer> supplyAndSync();
    List<PokerPlayer> getActivePlayers();
    PokerPlayer getCurrentPlayer();
    void add(PokerPlayer player);
    void remove(PokerPlayer player);
    void startOver();
}
