package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class PotCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final String INVALID_POT_AMOUNT = "Invalid pot amount: ";
    
    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            try {
                double potAmount = Double.parseDouble(args[1]);
                controller.updatePot((int) potAmount);
            } catch (NumberFormatException e) {
                System.err.println(INVALID_POT_AMOUNT + args[1]);
            }
        }
    }
}
