package ecr.ac.tec.lenguajes.pokerServer.game.managers;

import ecr.ac.tec.lenguajes.pokerServer.config.DefaultConfig;
import ecr.ac.tec.lenguajes.pokerServer.player.NetworkPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.server.ServerContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LobbyPlayerManager implements PlayerManager {
    private static final int MIN_PLAYERS_TO_START = 2;
    private static final int START_DELAY_SECONDS = 15;
    private static final String PLAYER_JOINED_PREFIX = "PLAYER_JOINED:";
    private static final String PLAYER_LIST_PREFIX = "PLAYER_LIST";
    private static final String SEPARATOR = ":";
    private static final String TIMER_ZERO = "0";
    private static final String LOBBY_PLAYER_ADDED_MSG = "Lobby: Player added. Total: ";
    private static final String LOBBY_STARTING_GAME_MSG = "Lobby: Starting game with ";
    private static final String PLAYERS_SUFFIX = " players...";
    private static final String GAME_STARTED_MSG = "Game Started, Switching to MapGameManager.";
    private static final String LOBBY_PLAYER_REMOVED_MSG = "Lobby: Player ";
    private static final String REMOVED_TOTAL_MSG = " removed. Total: ";
    private static final String PLAYER_LEFT_PREFIX = "PLAYER_LEFT:";
    private static final String LOBBY_TIMER_PREFIX = "LOBBY_TIMER:";
    private static final String PLAYER_MONEY_PREFIX = "PLAYER_MONEY:";
    private static final String LOBBY_TIMER_CANCELLED = "CANCELLED";
    private static final String MONEY_PREFIX = "MONEY:";
    private static final String GAME_STARTING_DELAY_MSG = "Lobby: Game starting in " + START_DELAY_SECONDS
            + " seconds...";
    private static final String GAME_START_CANCELLED_MSG = "Lobby: Player left. Waiting for more players...";

    private final List<PokerPlayer> pendingPlayers;
    private final ServerContext context;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> startTask;
    private int remainingSeconds;

    public LobbyPlayerManager(ServerContext context) {
        this.context = context;
        this.pendingPlayers = new ArrayList<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void addPlayer(PokerPlayer player) {
        if (player instanceof NetworkPokerPlayer newPlayer) {
            for (PokerPlayer pendingPlayer : pendingPlayers) {
                ((NetworkPokerPlayer) pendingPlayer).sendMessage(PLAYER_JOINED_PREFIX + newPlayer.getPlayerName());
            }

            StringBuilder playerList = new StringBuilder(PLAYER_LIST_PREFIX);
            for (PokerPlayer pendingPlayer : pendingPlayers) {
                playerList.append(SEPARATOR).append(((NetworkPokerPlayer) pendingPlayer).getPlayerName());
            }
            newPlayer.sendMessage(playerList.toString());

            String moneyMsg = PLAYER_MONEY_PREFIX + newPlayer.getPlayerName() + ":" + player.getTotalAmount();
            newPlayer.sendMessage(MONEY_PREFIX + player.getTotalAmount());

            for (PokerPlayer pendingPlayer : pendingPlayers) {
                if (pendingPlayer instanceof NetworkPokerPlayer np) {
                    np.sendMessage(moneyMsg);
                    newPlayer.sendMessage(
                            PLAYER_MONEY_PREFIX + np.getPlayerName() + SEPARATOR + pendingPlayer.getTotalAmount());
                }
            }
        }

        pendingPlayers.add(player);
        System.out.println(LOBBY_PLAYER_ADDED_MSG + pendingPlayers.size());

        checkStartGame();
    }

    private void checkStartGame() {
        if (pendingPlayers.size() >= MIN_PLAYERS_TO_START) {
            if (startTask == null || startTask.isDone() || startTask.isCancelled()) {
                System.out.println(GAME_STARTING_DELAY_MSG);
                remainingSeconds = START_DELAY_SECONDS;
                startTask = scheduler.scheduleAtFixedRate(this::timerTick, 0, 1, TimeUnit.SECONDS);
            }
        }
    }

    private void timerTick() {
        if (remainingSeconds > 0) {
            broadcastMessage(LOBBY_TIMER_PREFIX + remainingSeconds);
            remainingSeconds--;
        } else {
            broadcastMessage(LOBBY_TIMER_PREFIX + TIMER_ZERO);
            startGame();
        }
    }

    private void cancelStartGame() {
        if (startTask != null && !startTask.isDone()) {
            startTask.cancel(false);
            startTask = null;
            System.out.println(GAME_START_CANCELLED_MSG);
            broadcastMessage(LOBBY_TIMER_PREFIX + LOBBY_TIMER_CANCELLED);
        }
    }

    private void broadcastMessage(String message) {
        for (PokerPlayer player : pendingPlayers) {
            if (player instanceof NetworkPokerPlayer networkPlayer) {
                networkPlayer.sendMessage(message);
            }
        }
    }

    private void startGame() {
        if (startTask != null) {
            startTask.cancel(false);
            startTask = null;
        }
        System.out.println(LOBBY_STARTING_GAME_MSG + pendingPlayers.size() + PLAYERS_SUFFIX);
        DefaultConfig config = new DefaultConfig(pendingPlayers);
        config.initialize();
        GameManager realGameManager = config.getGameManager();
        realGameManager.startGame();
        context.setController(realGameManager);
        System.out.println(GAME_STARTED_MSG);
    }

    @Override
    public void removePlayer(PokerPlayer player) {
        pendingPlayers.remove(player);
        if (pendingPlayers.size() < MIN_PLAYERS_TO_START) {
            cancelStartGame();
        }
    }

    @Override
    public void removePlayerByName(String name) {
        boolean wasRemoved = pendingPlayers.removeIf(
                pokerPlayer -> ((NetworkPokerPlayer) pokerPlayer).getPlayerName().equals(name));
        if (wasRemoved) {
            System.out.println(LOBBY_PLAYER_REMOVED_MSG + name + REMOVED_TOTAL_MSG + pendingPlayers.size());
            for (PokerPlayer pokerPlayer : pendingPlayers) {
                ((NetworkPokerPlayer) pokerPlayer).sendMessage(PLAYER_LEFT_PREFIX + name);
            }

            if (pendingPlayers.size() < MIN_PLAYERS_TO_START) {
                cancelStartGame();
            }
        }
    }

    @Override
    public boolean hasPlayer(String name) {
        for (PokerPlayer player : pendingPlayers) {
            if (player instanceof NetworkPokerPlayer networkPlayer && networkPlayer.getPlayerName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
