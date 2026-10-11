package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;
import ecr.ac.tec.lenguajes.pokerGUI.model.game.GameType;

public class GameTypeCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final String LOG_INVALID_GAME_TYPE = "Invalid game type received: ";

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String gameTypeStr = args[1];
            try {
                GameType gameType = GameType.valueOf(gameTypeStr);
                controller.setGameMode(gameType);
            } catch (IllegalArgumentException e) {
                System.err.println(LOG_INVALID_GAME_TYPE + gameTypeStr);
            }
        }
    }
}
