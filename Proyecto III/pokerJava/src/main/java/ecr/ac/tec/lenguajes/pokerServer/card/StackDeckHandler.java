package ecr.ac.tec.lenguajes.pokerServer.card;

import java.util.Collections;
import java.util.Stack;

public final class StackDeckHandler implements DeckHandler {
    private final Stack<Card> deck;

    public StackDeckHandler() {
        this.deck = new Stack<>();
    }

    @Override
    public void fill() {
        deck.empty();
        for (final Suit suit : Suit.values()) {
            for (final Rank rank : Rank.values()) {
                deck.push(new Card(rank, suit));
            }
        }
    }

    @Override
    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    @Override
    public Card draw() {
        return this.deck.pop();
    }
}
