package ecr.ac.tec.lenguajes.pokerServer.config;

import ecr.ac.tec.lenguajes.pokerServer.card.StackDeckHandler;
import ecr.ac.tec.lenguajes.pokerServer.game.managers.GameManager;
import ecr.ac.tec.lenguajes.pokerServer.game.GameType;
import ecr.ac.tec.lenguajes.pokerServer.game.managers.MapGameManager;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.CommunityPokerDealer;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.DefaultCommunityPokerDealer;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.PokerDealer;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.StudPokerDealer;
import ecr.ac.tec.lenguajes.pokerServer.player.supplier.ListPlayerSupplier;
import ecr.ac.tec.lenguajes.pokerServer.player.supplier.PlayerSupplier;
import ecr.ac.tec.lenguajes.pokerServer.pot.PokerPot;
import ecr.ac.tec.lenguajes.pokerServer.pot.PokerPotDistributor;
import ecr.ac.tec.lenguajes.pokerServer.pot.SumPokerPot;
import ecr.ac.tec.lenguajes.pokerServer.table.CommunityTableConductor;
import ecr.ac.tec.lenguajes.pokerServer.table.StudTableConductor;
import ecr.ac.tec.lenguajes.pokerServer.table.TableConductor;
import ecr.ac.tec.lenguajes.pokerServer.table.mapper.MapTableConductorMapper;
import ecr.ac.tec.lenguajes.pokerServer.table.mapper.TableConductorMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultConfig implements Config {
    private static final int HOLDEM_HOLE_CARDS = 2;
    private static final int OMAHA_HOLE_CARDS = 4;
    private static final int EIGHT_OR_BETTER_HOLE_CARDS = 4;

    private static final int COMMUNITY_ROUNDS = 4;
    private static final int STUD_ROUNDS = 5;
    private static final double INITIAL_CONTRIBUTION = -1.0;

    private GameManager GAME_MANAGER;
    private final List<PokerPlayer> activePlayers;
    private static TableConductorMapper tableConductorMapper;

    public DefaultConfig(final List<PokerPlayer> activePlayers) {
        this.activePlayers = activePlayers;
    }

    @Override
    public void initialize() {
        CommunityPokerDealer holdemDealer = new DefaultCommunityPokerDealer(HOLDEM_HOLE_CARDS,  new StackDeckHandler());
        CommunityPokerDealer omahaDealer = new DefaultCommunityPokerDealer(OMAHA_HOLE_CARDS,  new StackDeckHandler());
        CommunityPokerDealer eightDealer = new DefaultCommunityPokerDealer(EIGHT_OR_BETTER_HOLE_CARDS,  new StackDeckHandler());
        PokerDealer razzDealer = new StudPokerDealer(new StackDeckHandler());
        PokerDealer sevenDealer = new StudPokerDealer(new StackDeckHandler());
        holdemDealer.reset();
        omahaDealer.reset();
        razzDealer.reset();
        sevenDealer.reset();
        eightDealer.reset();
        PokerPot pot = new SumPokerPot();
        tableConductorMapper = getTableMapper(holdemDealer, omahaDealer, razzDealer, sevenDealer, eightDealer);
    }

    private MapTableConductorMapper getTableMapper(final CommunityPokerDealer holdemDealer,
                                                   final CommunityPokerDealer omahaDealer,
                                                   final PokerDealer razzDealer,
                                                   final PokerDealer sevenDealer,
                                                   final CommunityPokerDealer eightDealer) {
        final TableConductor holdemTable = new CommunityTableConductor(
                new ArrayList<>(),
                new PokerPotDistributor(new HashMap<>(), new HashMap<>()),
                holdemDealer);
        holdemTable.loadPlayers(activePlayers);

        final Map<GameType, TableConductor> gamemodesMap = Map.of(
                GameType.HOLDEM, holdemTable,
                GameType.OMAHA, new CommunityTableConductor(
                        new ArrayList<>(),
                        new PokerPotDistributor(new HashMap<>(), new HashMap<>()),
                        omahaDealer),
                GameType.RAZZ, new StudTableConductor(
                        new PokerPotDistributor(new HashMap<>(), new HashMap<>()),
                        razzDealer),
                GameType.SEVEN_CARD_STUD, new StudTableConductor(
                        new PokerPotDistributor(new HashMap<>(), new HashMap<>()),
                        sevenDealer),
                GameType.EIGHT_OR_BETTER, new CommunityTableConductor(
                        new ArrayList<>(),
                        new PokerPotDistributor(new HashMap<>(), new HashMap<>()),
                        eightDealer)
        );
        return new MapTableConductorMapper(gamemodesMap);
    }

    private Map<GameType, Integer> getRoundsPerGamesType(){
        return Map.of(
                GameType.HOLDEM, COMMUNITY_ROUNDS,
                GameType.OMAHA, COMMUNITY_ROUNDS,
                GameType.RAZZ, STUD_ROUNDS,
                GameType.SEVEN_CARD_STUD, STUD_ROUNDS,
                GameType.EIGHT_OR_BETTER, COMMUNITY_ROUNDS
        );
    }

    private PlayerSupplier getPlayerSupplier() {
        return new ListPlayerSupplier(this.activePlayers);
    }

    private Map<PokerPlayer, Double> getPlayerContributions() {
        final Map<PokerPlayer, Double> contributions = new HashMap<>();
        for (PokerPlayer pokerPlayer : this.activePlayers) {
            contributions.put(pokerPlayer, INITIAL_CONTRIBUTION);
        }
        return contributions;
    }

    public GameManager getGameManager() {
        if (GAME_MANAGER == null) {
            GAME_MANAGER = new MapGameManager(
                    tableConductorMapper,
                    getRoundsPerGamesType(),
                    getPlayerContributions(),
                    getPlayerSupplier()
            );
        }
        return GAME_MANAGER;
    }

}
