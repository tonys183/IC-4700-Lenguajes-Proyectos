package ecr.ac.tec.lenguajes.pokerServer.pot;

import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.List;

public interface PotDistribuitor {
    void registerBet(PokerPlayer player, double amount);
    void distribute(List<PokerPlayer> winners);
    void createPots();
    void reset();
    double getTotalPot();
    double getPlayerBet(PokerPlayer player);
}
