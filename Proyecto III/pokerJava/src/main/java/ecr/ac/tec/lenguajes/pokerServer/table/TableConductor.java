package ecr.ac.tec.lenguajes.pokerServer.table;

import ecr.ac.tec.lenguajes.pokerServer.player.BetStatus;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.List;
import java.util.Optional;

public interface TableConductor {
    void reset();
    void loadPlayers(List<PokerPlayer> players);
    void startRound(double currentMaxBet);
    void startBet(BetStatus status, Optional<Double> amount, double currentMaxBet);
    void endGame(List<PokerPlayer> winners);
    double getPotAmount();
    void dealAll();
    void addSpectator(PokerPlayer player);
}
