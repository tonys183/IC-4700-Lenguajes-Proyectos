package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class GameCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final String ACTION_RESET = "RESET";
    private static final String MSG_NEW_GAME = "New Game Started";

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String action = args[1];
            if (ACTION_RESET.equals(action)) {
                controller.updateCommunityCards();
                controller.updatePlayerHand();
                controller.updatePot(0);
                controller.showGameStatus(MSG_NEW_GAME);
            }
        }
    }
}
