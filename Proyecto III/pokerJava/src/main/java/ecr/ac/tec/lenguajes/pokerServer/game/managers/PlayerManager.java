package ecr.ac.tec.lenguajes.pokerServer.game.managers;

import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;

public interface PlayerManager {
    void addPlayer(PokerPlayer player);
    void removePlayer(PokerPlayer player);
    void removePlayerByName(String playerName);
    boolean hasPlayer(String name);
}
