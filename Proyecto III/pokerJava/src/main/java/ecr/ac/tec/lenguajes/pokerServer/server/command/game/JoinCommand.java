package ecr.ac.tec.lenguajes.pokerServer.server.command.game;

import ecr.ac.tec.lenguajes.pokerServer.game.managers.PlayerManager;
import ecr.ac.tec.lenguajes.pokerServer.player.DefaultNetworkPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.server.ServerContext;
import java.io.PrintWriter;

public final class JoinCommand implements GameCommand {
    private static final String PLAYER_JOINED_LOG = "Player joined: ";
    private static final String WITH_MONEY_LOG = " with $";
    private static final String CANNOT_JOIN_LOG = "[ERROR] Cannot join: Game is already in progress or invalid state.";
    private static final String ERROR_GAME_IN_PROGRESS = "[ERROR] Game in progress";
    private static final String LOG_NAME_TAKEN = "Cannot join: Name ";
    private static final String LOG_IS_ALREADY_TAKEN = " is already taken.";
    private static final String ERROR_NAME_TAKEN = "ERROR:NAME_TAKEN";

    private final String playerName;
    private final double initialMoney;
    private final PrintWriter out;

    public JoinCommand(String playerName, double initialMoney, PrintWriter out) {
        this.playerName = playerName;
        this.initialMoney = initialMoney;
        this.out = out;
    }

    @Override
    public void execute(ServerContext context) {
        PlayerManager controller = context.getController();
        if (controller != null) {
            if (controller.hasPlayer(playerName)) {
                System.err.println(LOG_NAME_TAKEN + playerName + LOG_IS_ALREADY_TAKEN);
                out.println(ERROR_NAME_TAKEN);
                return;
            }
            PokerPlayer player = new DefaultNetworkPokerPlayer(playerName, initialMoney, out);
            controller.addPlayer(player);
            System.out.println(PLAYER_JOINED_LOG + playerName + WITH_MONEY_LOG + initialMoney);
        } else {
            System.err.println(CANNOT_JOIN_LOG);
            out.println(ERROR_GAME_IN_PROGRESS);
        }
    }
}