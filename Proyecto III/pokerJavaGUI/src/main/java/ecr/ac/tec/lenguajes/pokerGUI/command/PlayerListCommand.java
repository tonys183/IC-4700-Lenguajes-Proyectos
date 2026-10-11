package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class PlayerListCommand implements ClientCommand {
    @Override
    public void execute(FXMLTableController controller, String[] args) {
        for (int i = 1; i < args.length; i++) {
            String playerName = args[i];
            controller.addOpponent(playerName);
        }
    }
}
