package ecr.ac.tec.lenguajes.pokerServer.server.command.game;

import ecr.ac.tec.lenguajes.pokerServer.game.managers.PlayerManager;
import ecr.ac.tec.lenguajes.pokerServer.server.ServerContext;

public class LeaveCommand implements GameCommand {
    private final String playerName;

    public LeaveCommand(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void execute(ServerContext context) {
        if (context.getController() != null) {
            PlayerManager manager = context.getController();
            manager.removePlayerByName(playerName);
        }
    }
}
