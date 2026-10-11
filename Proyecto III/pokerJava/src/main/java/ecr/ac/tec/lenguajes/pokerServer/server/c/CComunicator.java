package ecr.ac.tec.lenguajes.pokerServer.server.c;

import ecr.ac.tec.lenguajes.pokerServer.game.GameType;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.List;

public interface CComunicator {
    String buildMessage(List<PokerPlayer> players, GameType gameType);
    List<PokerPlayer>  parseWinners(String winners, List<PokerPlayer> players, GameType gameType);
}
