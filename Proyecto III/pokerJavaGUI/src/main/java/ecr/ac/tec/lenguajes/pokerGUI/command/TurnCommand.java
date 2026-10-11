package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;

public class TurnCommand implements ClientCommand {
    private static final int MIN_ARGS = 2;
    private static final int CALL_AMOUNT_INDEX = 3;
    private static final int FOLD_ENABLED_INDEX = 2;
    private static final int INITIAL_COMMUNITY_CARDS = 0;
    private static final String SB = "SB";
    private static final String BB = "BB";
    private static final String YOURN_TURN = "YOUR_TURN";

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            String status = args[1];
            if (YOURN_TURN.equals(status)) {
                if (!controller.isFolded()) {
                    String role = controller.getPlayerRole();
                    int communityCards = controller.getCommunityCardCount();
                    boolean canFold = true;
                    if (args.length > FOLD_ENABLED_INDEX) {
                        canFold = Boolean.parseBoolean(args[FOLD_ENABLED_INDEX]);
                    } else if (communityCards == INITIAL_COMMUNITY_CARDS && (SB.equals(role) || BB.equals(role))) {
                        canFold = false;
                    }
                    
                    double callAmount = 0.0;
                    if (args.length > CALL_AMOUNT_INDEX) {
                        try {
                            callAmount = Double.parseDouble(args[CALL_AMOUNT_INDEX]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    controller.updateCallButton(callAmount);
                    controller.setControlsEnabled(true, canFold);
                }
            } else {
                controller.setControlsEnabled(false);
            }
        }
    }
}
