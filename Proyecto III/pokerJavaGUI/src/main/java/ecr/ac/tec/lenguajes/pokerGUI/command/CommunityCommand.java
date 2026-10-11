package ecr.ac.tec.lenguajes.pokerGUI.command;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Card;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Rank;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Suit;

public class CommunityCommand implements ClientCommand {
    private static final int MIN_ARGS = 3;

    @Override
    public void execute(FXMLTableController controller, String[] args) {
        if (args.length >= MIN_ARGS) {
            Rank rank = Rank.valueOf(args[1]);
            Suit suit = Suit.valueOf(args[2]);
            controller.addCommunityCard(new Card(rank, suit));
        }
    }
}
