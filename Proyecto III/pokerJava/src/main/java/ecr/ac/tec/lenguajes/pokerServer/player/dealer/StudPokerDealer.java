package ecr.ac.tec.lenguajes.pokerServer.player.dealer;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.card.DeckHandler;
import ecr.ac.tec.lenguajes.pokerServer.player.NetworkPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.List;

public class StudPokerDealer implements PokerDealer {
    private static final int CARDS_PER_PLAYER_ROUND_1 = 3;
    private static final int CARDS_PER_PLAYER_ROUND_THEN_FIRST = 1;
    private static final int NUMBER_OF_ROUNDS = 5;
    private static final int HAND_SIZE_VISIBLE_3 = 3;
    private static final int HAND_SIZE_VISIBLE_MIN_4 = 4;
    private static final int HAND_SIZE_VISIBLE_MAX_6 = 6;
    private static final int HAND_SIZE_SEVENTH_STREET = 7;
    private static final String OPPONENT_CARD_PREFIX = "OPPONENT_CARD:";
    private static final String CARD_BACK = ":BACK";
    private static final String SEPARATOR = ":";

    private int round;
    private final DeckHandler deck;

    public StudPokerDealer(DeckHandler deck) {
        this.deck = deck;
        this.round = 0;
    }

    @Override
    public void reset() {
        deck.fill();
        deck.shuffle();
    }

    @Override
    public void deal(List<PokerPlayer> players) {
        final int cardsPerPlayer = round == 0 ? CARDS_PER_PLAYER_ROUND_1 : CARDS_PER_PLAYER_ROUND_THEN_FIRST;

        for (int i = 0; i < cardsPerPlayer; i++) {
            for (PokerPlayer player : players) {
                Card card = deck.draw();

                int handSize = player.show().show().size() + 1;
                boolean isVisible = (handSize == HAND_SIZE_VISIBLE_3)
                        || (handSize >= HAND_SIZE_VISIBLE_MIN_4 && handSize <= HAND_SIZE_VISIBLE_MAX_6);
                boolean isSeventhStreet = (handSize == HAND_SIZE_SEVENTH_STREET);

                player.receiveCard(card, isVisible);

                if (isVisible) {
                    String msg = OPPONENT_CARD_PREFIX + ((NetworkPokerPlayer) player).getPlayerName()
                            + SEPARATOR + card.rank() + SEPARATOR + card.suit();
                    broadcast(players, player, msg);
                } else if (isSeventhStreet) {
                    String msg = OPPONENT_CARD_PREFIX + ((NetworkPokerPlayer) player).getPlayerName() + CARD_BACK;
                    broadcast(players, player, msg);
                } else {
                    String msg = OPPONENT_CARD_PREFIX + ((NetworkPokerPlayer) player).getPlayerName() + CARD_BACK;
                    broadcast(players, player, msg);
                }
            }
        }
        round++;
        round %= NUMBER_OF_ROUNDS;
    }

    private void broadcast(List<PokerPlayer> players, PokerPlayer source, String message) {
        for (PokerPlayer player : players) {
            if (player != source) {
                ((NetworkPokerPlayer) player).sendMessage(message);
            }
        }
    }

}
