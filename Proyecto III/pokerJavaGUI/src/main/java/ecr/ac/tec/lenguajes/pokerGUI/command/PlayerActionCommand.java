package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class PlayerActionCommand implements ClientCommand {
    private static final int MIN_ARGS = 3;
    private static final String EMPTY = "";    

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String playerName = args[1];
            String action = args[2];
            String amount = (args.length > MIN_ARGS) ? args[3] : EMPTY;

            String displayAction = action;
            if (!amount.isEmpty()) {
                displayAction += " " + amount;
            }

            controller.setPlayerAction(playerName, displayAction);
        }
    }
}
