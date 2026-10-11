package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class StatusCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final String STATUS_PREFIX = "Game Status: ";
    
    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String status = args[1];
            System.out.println(STATUS_PREFIX + status);
        }
    }


}
