package poker.hand;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.card.Rank;
import ecr.ac.tec.lenguajes.pokerServer.card.Suit;
import java.util.ArrayList;
import java.util.List;

import ecr.ac.tec.lenguajes.pokerServer.hand.DownHandRevealer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class DownHandRevealerTest {
    private static DownHandRevealer revealer;
    private static final int EXPECTED_CARDS_SIZE = 2;

    @BeforeEach
    public void setup() {
        revealer = new DownHandRevealer();
    }

    @Test
    public void receiveCardsTest() {
        // given
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Rank.TWO, Suit.HEARTS));
        cards.add(new Card(Rank.ACE, Suit.HEARTS));

        revealer.receiveCard(new Card(Rank.TWO, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.ACE, Suit.HEARTS));

        // when + then
        assertEquals(EXPECTED_CARDS_SIZE, revealer.show().size());
        assertEquals(cards, revealer.show());
    }
}
