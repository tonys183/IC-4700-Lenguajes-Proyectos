package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class OpponentCardCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final int MAX_ARGS = 4;
    private static final String ACTION_BACK = "BACK";
    private static final String ACTION_EMPTY = "";


    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String playerName = args[1];
            String rank = ACTION_BACK;
            String suit = ACTION_EMPTY;
            if (args.length >= MAX_ARGS) {
                rank = args[2];
                suit = args[3];
            }
            controller.addOpponentCard(playerName, rank, suit);
        }
    }
}
