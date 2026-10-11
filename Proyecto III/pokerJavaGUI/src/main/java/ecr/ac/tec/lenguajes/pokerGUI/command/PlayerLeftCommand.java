package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class PlayerLeftCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    
    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String playerName = args[1];
            controller.removeOpponent(playerName);
        }
    }
}
