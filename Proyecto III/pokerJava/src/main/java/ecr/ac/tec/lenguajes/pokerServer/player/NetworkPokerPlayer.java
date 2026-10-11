package ecr.ac.tec.lenguajes.pokerServer.player;

public interface NetworkPokerPlayer extends PokerPlayer {
    String getPlayerName();
    void sendMessage(String message);
}
