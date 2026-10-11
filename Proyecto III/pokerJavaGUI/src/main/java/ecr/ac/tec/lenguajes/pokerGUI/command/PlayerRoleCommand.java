package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class PlayerRoleCommand implements ClientCommand {
    private static final int MIN_ARGS = 3;
    
    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String playerName = args[1];
            String role = args[2];
            controller.setPlayerRole(playerName, role);
        }
    }
}
