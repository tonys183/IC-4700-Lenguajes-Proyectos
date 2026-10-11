package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class WinnerCommand implements ClientCommand {
    private static final String CARD_SEPARATOR = ",";

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length < 3) {
            return;
        }
        String winnerName = args[1];
        Map<String, List<String>> playerHands = new HashMap<>();
        if (args.length >= 3) {
            playerHands.put(winnerName, Arrays.asList(args[2].split(CARD_SEPARATOR)));
        }
        
        for (int i = 3; i < args.length; i += 2) {
            if (i+1 < args.length) {
                String pName = args[i];
                String pCards = args[i+1];
                playerHands.put(pName, Arrays.asList(pCards.split(CARD_SEPARATOR)));
            }
        }
        
        controller.showWinner(winnerName, playerHands);
    }
}
