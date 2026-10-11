package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class ErrorCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final String LOG_SERVER_ERROR = "Server Error: ";

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String errorMessage = args[1];
            System.err.println(LOG_SERVER_ERROR + errorMessage);
        }
    }
}
