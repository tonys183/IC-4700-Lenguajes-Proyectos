package ecr.ac.tec.lenguajes.pokerServer.game.managers;

import ecr.ac.tec.lenguajes.pokerServer.game.GameType;
import ecr.ac.tec.lenguajes.pokerServer.player.BetStatus;
import ecr.ac.tec.lenguajes.pokerServer.player.NetworkPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.supplier.PlayerSupplier;
import ecr.ac.tec.lenguajes.pokerServer.table.mapper.TableConductorMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MapGameManager implements GameManager {
    private static final int TOTAL_GAMES = 5;
    private static final double INITIAL_MAX_BET = 10.0;
    private static final double MIN_DOUBLED_BET = 20.0;
    private static final double RESET_MAX_BET = 0.0;
    private static final double RESET_CONTRIBUTION = -1.0;
    private static final int WAIT_TIME = 10000;
    private static final String PLAYER_LEFT_MSG = "PLAYER_LEFT:";
    private static final String PLAYER_REMOVED_LOG = "Game: Player ";
    private static final String REMOVED_SUFFIX = " removed.";
    private static final String POT_MSG = "POT:";
    private static final String GAME_TYPE_MSG = "GAME_TYPE:";

    private final TableConductorMapper gamemodes;
    private final Map<GameType, Integer> roundsPerGameType;
    private final Map<PokerPlayer, Double> currentRoundContributions;
    private final PlayerSupplier playerSupplier;
    private int currentGameType;
    private int currentGameTypeRound;
    private double currentMaxBetAmount;

    public MapGameManager(
            final TableConductorMapper gamemodes,
            final Map<GameType, Integer> roundsPerGameType,
            final Map<PokerPlayer, Double> currentRoundContributions,
            final PlayerSupplier playerSupplier) {
        this.gamemodes = gamemodes;
        this.roundsPerGameType = roundsPerGameType;
        this.currentRoundContributions = currentRoundContributions;
        this.currentGameTypeRound = this.currentGameType = 0;
        this.currentMaxBetAmount = INITIAL_MAX_BET;
        this.playerSupplier = playerSupplier;
    }

    @Override
    public void addPlayer(final PokerPlayer player) {
        this.playerSupplier.add(player);
        if (player instanceof NetworkPokerPlayer networkPlayer) {
             networkPlayer.sendMessage(GAME_TYPE_MSG + GameType.values()[currentGameType].toString());
             double totalPot = this.gamemodes.get(GameType.values()[currentGameType]).getPotAmount();
             networkPlayer.sendMessage(POT_MSG + totalPot);
        }

        this.gamemodes.get(GameType.values()[currentGameType]).addSpectator(player);
    }

    @Override
    public void removePlayer(final PokerPlayer player) {
        this.playerSupplier.remove(player);
        currentRoundContributions.remove(player);
    }

    @Override
    public void removePlayerByName(String playerName) {
        Optional<PokerPlayer> playerToRemove = playerSupplier.getActivePlayers().stream()
                .filter(player -> ((NetworkPokerPlayer) player).getPlayerName().equals(playerName))
                .findFirst();

        if (playerToRemove.isPresent()) {
            PokerPlayer player = playerToRemove.get();
            removePlayer(player);

            for (PokerPlayer playerActive : playerSupplier.getActivePlayers()) {
                ((NetworkPokerPlayer) playerActive).sendMessage(PLAYER_LEFT_MSG + playerName);
            }
            System.out.println(PLAYER_REMOVED_LOG + playerName + REMOVED_SUFFIX);
        }
    }

    @Override
    public boolean hasPlayer(String name) {
        return false;
    }

    @Override
    public void reset() {
        for (GameType game : GameType.values()) {
            gamemodes.get(game).reset();
        }
        this.currentGameTypeRound = this.currentGameType = 0;
        this.currentMaxBetAmount = RESET_MAX_BET;
    }

    @Override
    public boolean playerBet(final BetStatus status, Optional<Double> amount) {
        PokerPlayer currentPlayer = playerSupplier.getCurrentPlayer();
        return doPlayerBet(currentPlayer, status, amount);
    }

    private boolean doPlayerBet(PokerPlayer currentPlayer, final BetStatus status, Optional<Double> amount) {
        if (status == BetStatus.RAISE && amount.isPresent()) {
            Optional<Boolean> result = processRaise(amount.get(), amount, currentPlayer);
            if (result.isPresent()) {
                return result.get();
            }
        }

        amount = calculateBetAmount(status, amount, currentPlayer);
        updateContribution(status, amount, currentPlayer);
        applyBlindsRule(currentPlayer);
        this.gamemodes.get(GameType.values()[currentGameType]).startBet(status, amount, currentMaxBetAmount);
        broadcastPot();
        return handleRoundEnd();
    }

    private Optional<Boolean> processRaise(double raiseAmount, Optional<Double> amount, PokerPlayer currentPlayer) {
        if (raiseAmount < currentMaxBetAmount) {
            double lastContribution = currentRoundContributions.getOrDefault(currentPlayer, 0.0);
            double amountToAdd = raiseAmount - lastContribution;

            if (amountToAdd >= currentPlayer.getTotalAmount()) {
                return Optional.of(doPlayerBet(currentPlayer, BetStatus.ALL_IN, amount));
            }

            return Optional.of(doPlayerBet(currentPlayer, BetStatus.CALL, Optional.empty()));
        }

        double difference = raiseAmount - currentMaxBetAmount;
        currentMaxBetAmount = Math.max(raiseAmount, currentMaxBetAmount);
        return Optional.empty();
    }

    private Optional<Double> calculateBetAmount(BetStatus status, Optional<Double> amount, PokerPlayer currentPlayer) {
        double lastContribution = Math.max(0.0, currentRoundContributions.getOrDefault(currentPlayer, 0.0));
        double callBet = Math.min(currentMaxBetAmount - lastContribution, currentPlayer.getTotalAmount());
        return status == BetStatus.CALL ? Optional.of(callBet) : amount;
    }

    private void updateContribution(BetStatus status, Optional<Double> amount, PokerPlayer currentPlayer) {
        if (amount.isPresent()) {
            double newTotal = (status == BetStatus.CALL) ? currentMaxBetAmount : amount.get();
            currentRoundContributions.put(currentPlayer, newTotal);
        }
    }

    private void applyBlindsRule(PokerPlayer currentPlayer) {
        long playersWithBets = currentRoundContributions.values().stream().filter(v -> v > 0).count();
        if (currentGameTypeRound == 1 && currentPlayer.isSmallBlind() && playersWithBets == 1) {
            currentMaxBetAmount = currentMaxBetAmount > INITIAL_MAX_BET ? currentMaxBetAmount : MIN_DOUBLED_BET;
        }
    }

    private void broadcastPot() {
        double totalPot = this.gamemodes.get(GameType.values()[currentGameType]).getPotAmount();
        for (PokerPlayer player : playerSupplier.getActivePlayers()) {
            ((NetworkPokerPlayer) player).sendMessage(POT_MSG + totalPot);
        }
    }

    private boolean handleRoundEnd() {
        boolean roundFinished = isPotEven();
        if (roundFinished) {
            this.currentMaxBetAmount = RESET_MAX_BET;
            playerSupplier.startOver();
            for (PokerPlayer player : currentRoundContributions.keySet()) {
                currentRoundContributions.replace(player, RESET_CONTRIBUTION);
                player.setTurn(false);
            }
        }
        return roundFinished;
    }

    @Override
    public boolean doCurrentRound() {
        if (currentGameTypeRound < roundsPerGameType.get(GameType.values()[currentGameType])) {
            this.gamemodes.get(GameType.values()[currentGameType]).startRound(currentMaxBetAmount);
            this.currentGameTypeRound++;
            return false;
        }
        return true;
    }

    @Override
    public boolean finishCurrentGame(final List<PokerPlayer> winners) {
        this.gamemodes.get(GameType.values()[currentGameType]).endGame(winners);
        try {
            Thread.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.gamemodes.get(GameType.values()[currentGameType]).reset();
        this.currentGameType++;
        if (this.currentGameType == TOTAL_GAMES) {
            this.currentGameType = 0;
        }
        this.currentGameTypeRound = 0;
        this.currentMaxBetAmount = INITIAL_MAX_BET;
        return syncNextGameType();
    }

    @Override
    public GameType getCurrentGame() {
        return GameType.values()[currentGameType];
    }

    @Override
    public Optional<PokerPlayer> winnerByFold() {
        Optional player = Optional.empty();
        boolean wonByFold = false;
        for (PokerPlayer pokerPlayer : playerSupplier.getActivePlayers()) {
            if (pokerPlayer.isActive() && wonByFold) {
                return Optional.empty();
            }
            if (pokerPlayer.isActive()) {
                player = Optional.of(pokerPlayer);
                wonByFold = true;
            }
        }
        return player;
    }

    @Override
    public List<PokerPlayer> activePlayers() {
        return this.playerSupplier.getActivePlayers().stream().filter(PokerPlayer::isActive).toList();
    }

    private boolean syncNextGameType() {
        boolean hasFinishedAllGames = this.currentGameType == TOTAL_GAMES;
        Collections.rotate(this.playerSupplier.getActivePlayers(), 1);
        final List<PokerPlayer> newPlayers = this.playerSupplier.supplyAndSync();
        currentRoundContributions.clear();
        for (PokerPlayer player : newPlayers) {
            currentRoundContributions.put(player, 0.0);
        }
        if (!hasFinishedAllGames) {
            System.out.println(GameType.values()[currentGameType].toString());
            String newGameType = GameType.values()[currentGameType].toString();
            for (PokerPlayer pokerPlayer : playerSupplier.getActivePlayers()) {
                NetworkPokerPlayer networkPokerPlayer = ((NetworkPokerPlayer) pokerPlayer);
                networkPokerPlayer.sendMessage(GAME_TYPE_MSG + newGameType);
                networkPokerPlayer.setTurn(false);
            }
            this.gamemodes.get(GameType.values()[currentGameType]).loadPlayers(newPlayers);
            this.currentMaxBetAmount = INITIAL_MAX_BET;
        }
        return hasFinishedAllGames;
    }

    @Override
    public void startGame() {
        this.currentMaxBetAmount = INITIAL_MAX_BET;
        doCurrentRound();
    }

    private boolean isPotEven() {
        for (PokerPlayer player : playerSupplier.getActivePlayers()) {
            Double contribution = currentRoundContributions.get(player);
            if (player.getTotalAmount() > 0 && player.isActive()
                    && (contribution == null || contribution != currentMaxBetAmount)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean shouldRoundEnd() {
        boolean shouldEnd = true;
        for (PokerPlayer player : this.playerSupplier.getActivePlayers()) {
            if (player.getTotalAmount() > 0) {
                shouldEnd = false;
                break;
            }
        }
        if (shouldEnd) {
            gamemodes.get(GameType.values()[currentGameType]).dealAll();
        }
        return shouldEnd;
    }
}
