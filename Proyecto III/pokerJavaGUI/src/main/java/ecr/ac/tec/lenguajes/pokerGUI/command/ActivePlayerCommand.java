package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class ActivePlayerCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String activePlayerName = args[1];
            controller.setActivePlayer(activePlayerName);
        }
    }
}
