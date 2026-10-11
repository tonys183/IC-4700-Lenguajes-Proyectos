package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class PlayerJoinedCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final String DEFAULT_MONEY = "0";
    
    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String playerName = args[1];
            String money = DEFAULT_MONEY;
            if (args.length > 2) {
                money = args[2];
            }
            controller.addOpponent(playerName, money);
        }
    }
}
