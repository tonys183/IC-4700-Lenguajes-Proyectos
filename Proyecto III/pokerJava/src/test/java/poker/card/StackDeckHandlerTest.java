package poker.card;

import ecr.ac.tec.lenguajes.pokerServer.card.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class StackDeckHandlerTest {
    private static DeckHandler deckHandler;
    private static final int FULL_DECK_SIZE = 52;

    @BeforeEach
    public void setup() {
        deckHandler = new StackDeckHandler();
    }

    @Test
    public void fillTest() {
        //given
        deckHandler.fill();

        // when
        int cardCount = 0;
        while (true) {
            try {
                deckHandler.draw();
                cardCount++;
            } catch (Exception e) {
                break;
            }
        }

        // then
        assertEquals(FULL_DECK_SIZE, cardCount);
    }

    @Test
    public void shuffleTest() {
        //given
        deckHandler.fill();
        Card firstCard = deckHandler.draw();

        // when
        deckHandler.shuffle();

        // then
        assertNotEquals(firstCard, deckHandler.draw());
    }

    @Test
    public void drawtest() {
        //given
        Card card = new Card(Rank.ACE, Suit.HEARTS);
        deckHandler.fill();

        // when + then
        assertNotEquals(card, deckHandler.draw());
    }

}
