package ecr.ac.tec.lenguajes.pokerServer.player.supplier;

import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.ArrayList;
import java.util.List;

public final class ListPlayerSupplier implements PlayerSupplier {
    private final List<PokerPlayer> activePlayers;
    private final List<PokerPlayer> waitingForJoinPlayers;
    private final List<PokerPlayer> waitingForGoPlayers;
    private static final int NEW_PLAYERS_POSITION = 2;
    private int currentPlayer;

    public ListPlayerSupplier(final List<PokerPlayer> activePlayers) {
        this.activePlayers = activePlayers;
        this.waitingForJoinPlayers = new ArrayList<>();
        this.waitingForGoPlayers = new ArrayList<>();
        this.currentPlayer = 0;
    }

    @Override
    public List<PokerPlayer> supplyAndSync() {
        activePlayers.removeAll(waitingForGoPlayers);
        List<PokerPlayer> newPlayers = new ArrayList<>(waitingForJoinPlayers);
        waitingForJoinPlayers.clear();
        this.currentPlayer = 0;
        activePlayers.addAll(Math.min(NEW_PLAYERS_POSITION, activePlayers.size()), newPlayers);
        return this.activePlayers;
    }

    @Override
    public void add(PokerPlayer player) {
        waitingForJoinPlayers.add(player);
    }

    @Override
    public void remove(PokerPlayer player) {
        waitingForJoinPlayers.remove(player);
        waitingForGoPlayers.add(player);
        player.fold();
    }

    @Override
    public void startOver() {
        this.currentPlayer = 0;
        while (this.currentPlayer < activePlayers.size() && !activePlayers.get(this.currentPlayer).isActive()) {
            this.currentPlayer++;
        }
        if (this.currentPlayer >= activePlayers.size()) {
            this.currentPlayer = 0;
        }
    }

    @Override
    public List<PokerPlayer> getActivePlayers() {
        return this.activePlayers;
    }

    @Override
    public PokerPlayer getCurrentPlayer() {
        PokerPlayer player = activePlayers.get(currentPlayer);
        int attempts = 0;
        do {
            currentPlayer = (currentPlayer + 1) % activePlayers.size();
            attempts++;
        } while (!activePlayers.get(currentPlayer).isActive() && attempts < activePlayers.size());
        return player;
    }
}
