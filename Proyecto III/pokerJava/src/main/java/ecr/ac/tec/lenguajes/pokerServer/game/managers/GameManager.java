package ecr.ac.tec.lenguajes.pokerServer.game.managers;

import ecr.ac.tec.lenguajes.pokerServer.game.GameType;
import ecr.ac.tec.lenguajes.pokerServer.player.BetStatus;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.List;
import java.util.Optional;

public interface GameManager extends PlayerManager {
    void reset();
    boolean playerBet(final BetStatus status, final Optional<Double> amount);
    boolean doCurrentRound();
    boolean finishCurrentGame(final List<PokerPlayer> winners);
    GameType getCurrentGame();
    List<PokerPlayer> activePlayers();
    void startGame();
    Optional<PokerPlayer> winnerByFold();
    boolean shouldRoundEnd();
}
