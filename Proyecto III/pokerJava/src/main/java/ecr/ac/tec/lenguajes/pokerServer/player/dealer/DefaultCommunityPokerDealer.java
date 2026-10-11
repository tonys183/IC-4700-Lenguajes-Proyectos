package ecr.ac.tec.lenguajes.pokerServer.player.dealer;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.card.DeckHandler;
import ecr.ac.tec.lenguajes.pokerServer.player.NetworkPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.ArrayList;
import java.util.List;

public final class DefaultCommunityPokerDealer implements CommunityPokerDealer {
    private static final String OPPONENT_CARD_PREFIX = "OPPONENT_CARD:";
    private static final String CARD_BACK = ":BACK";
    private static final int FLOP_CARDS = 3;
    private static final int FLOP_INDEX_OFFSET_1 = 3;
    private static final int FLOP_INDEX_OFFSET_2 = 2;
    private static final int FLOP_INDEX_OFFSET_3 = 1;

    private final int cardPerPlayer;
    private final DeckHandler deck;

    public DefaultCommunityPokerDealer(int cardPerPlayer, DeckHandler deck) {
        this.cardPerPlayer = cardPerPlayer;
        this.deck = deck;
    }

    @Override
    public void reset() {
        deck.fill();
        deck.shuffle();
    }

    @Override
    public void deal(List<PokerPlayer> players) {
        for (int i = 0; i < cardPerPlayer; i++) {
            for (PokerPlayer player : players) {
                player.receiveCard(deck.draw());
                String msg = OPPONENT_CARD_PREFIX + ((NetworkPokerPlayer) player).getPlayerName() + CARD_BACK;
                broadcast(players, player, msg);
            }
        }
    }

    private void broadcast(List<PokerPlayer> players, PokerPlayer source, String message) {
        for (PokerPlayer player : players) {
            if (player != source) {
                ((NetworkPokerPlayer)player).sendMessage(message);
            }
        }
    }

    @Override
    public void flop(List<Card> communityCards, List<PokerPlayer> players) {
        communityCards.add(deck.draw());
        communityCards.add(deck.draw());
        communityCards.add(deck.draw());

        int size = communityCards.size();
        for (PokerPlayer player : players) {
            player.receiveCommunityCard(communityCards.get(size - FLOP_INDEX_OFFSET_1));
            player.receiveCommunityCard(communityCards.get(size - FLOP_INDEX_OFFSET_2));
            player.receiveCommunityCard(communityCards.get(size - FLOP_INDEX_OFFSET_3));
        }
    }

    @Override
    public void turn(List<Card> communityCards, List<PokerPlayer> players) {
        communityCards.add(deck.draw());
        Card card = communityCards.getLast();
        for (PokerPlayer player : players) {
            player.receiveCommunityCard(card);
        }
    }

    @Override
    public void river(List<Card> communityCards, List<PokerPlayer> players) {
        communityCards.add(deck.draw());
        Card card = communityCards.getLast();
        for (PokerPlayer player : players) {
            player.receiveCommunityCard(card);
        }
    }
}
