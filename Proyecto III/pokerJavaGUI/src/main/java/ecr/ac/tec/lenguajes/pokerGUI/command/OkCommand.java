package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class OkCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final String CONFIRMATION_MESSAGE = "Server confirmed: ";

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            System.out.println(CONFIRMATION_MESSAGE + args[1]);
        }
    }
}
