package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Card;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Rank;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Suit;

public class CardCommand implements ClientCommand {
    private static final int MIN_ARGS = 3;
    private static final int MIN_ARGS_WITH_VISIBILITY = 4;
    private static final int VISIBILITY_INDEX = 3;
    private static final String LOG_RECEIVED_CARD = "[DEBUG]: Received card: ";
    private static final String LOG_OF = " of ";
    private static final String LOG_VISIBLE = " Visible: ";
    private static final String LOG_INVALID_ARGS = "[DEBUG]: Invalid CARD command args: ";

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            Rank rank = Rank.valueOf(args[1]);
            Suit suit = Suit.valueOf(args[2]);
            boolean isVisible = false;
            if (args.length >= MIN_ARGS_WITH_VISIBILITY) {
                isVisible = Boolean.parseBoolean(args[VISIBILITY_INDEX]);
            }
            System.out.println(LOG_RECEIVED_CARD + rank + LOG_OF + suit + LOG_VISIBLE + isVisible);
            controller.addCardToHand(new Card(rank, suit), isVisible);
        } else {
            System.out.println(LOG_INVALID_ARGS + args.length);
        }
    }
}
