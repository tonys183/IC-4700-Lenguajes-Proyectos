package poker.hand;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.card.Rank;
import ecr.ac.tec.lenguajes.pokerServer.card.Suit;
import java.util.ArrayList;
import java.util.List;

import ecr.ac.tec.lenguajes.pokerServer.hand.UpDownHandPartialRevealer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class UpDownHandPartialRevealerTest {
    private static UpDownHandPartialRevealer revealer;
    private static final int EXPECTED_REVEALED_CARDS_FIRST_ROUND = 1;
    private static final int EXPECTED_ALL_CARDS_FIRST_ROUND = 3;
    private static final int EXPECTED_REVEALED_CARDS_SECOND_ROUND = 4;
    private static final int EXPECTED_ALL_CARDS_SECOND_ROUND = 6;
    private static final int EXPECTED_ALL_CARDS_THIRD_ROUND = 7;

    @BeforeEach
    public void setup() {
        revealer = new UpDownHandPartialRevealer();
    }

    @Test
    public void receiveCardsFirstRound() {
        // given
        revealer.receiveCard(new Card(Rank.TWO, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.THREE, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.FOUR, Suit.HEARTS));

        // when
        List<Card> revealedCards = revealer.showPartial();
        List<Card> allCards = revealer.show();

        // then
        assertEquals(EXPECTED_REVEALED_CARDS_FIRST_ROUND, revealedCards.size());
        assertEquals(Rank.FOUR, revealedCards.getFirst().rank());
        assertEquals(Suit.HEARTS, revealedCards.getFirst().suit());
        assertEquals(EXPECTED_ALL_CARDS_FIRST_ROUND, allCards.size());
    }

    @Test
    public void receiveCardsSecondRound() {
        // given
        revealer.receiveCard(new Card(Rank.TWO, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.THREE, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.FOUR, Suit.HEARTS));

        revealer.receiveCard(new Card(Rank.FIVE, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.SIX, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.SEVEN, Suit.HEARTS));

        // when
        List<Card> revealedCards = revealer.showPartial();
        List<Card> allCards = revealer.show();

        // then
        assertEquals(EXPECTED_REVEALED_CARDS_SECOND_ROUND, revealedCards.size());
        assertEquals(Rank.FOUR, revealedCards.getFirst().rank());
        assertEquals(Suit.HEARTS, revealedCards.getFirst().suit());
        assertEquals(EXPECTED_ALL_CARDS_SECOND_ROUND, allCards.size());
    }

    @Test
    public void receiveCardsThirdRound() {
        // given
        List<Card> firstRoundCards = new ArrayList<>();
        firstRoundCards.add(new Card(Rank.TWO, Suit.HEARTS));
        firstRoundCards.add(new Card(Rank.THREE, Suit.HEARTS));
        firstRoundCards.add(new Card(Rank.FOUR, Suit.HEARTS));

        List<Card> secondRoundCards = new ArrayList<>();
        secondRoundCards.add(new Card(Rank.FIVE, Suit.HEARTS));
        secondRoundCards.add(new Card(Rank.SIX, Suit.HEARTS));
        secondRoundCards.add(new Card(Rank.SEVEN, Suit.HEARTS));

        revealer.receiveCard(new Card(Rank.TWO, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.THREE, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.FOUR, Suit.HEARTS));

        revealer.receiveCard(new Card(Rank.FIVE, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.SIX, Suit.HEARTS));
        revealer.receiveCard(new Card(Rank.SEVEN, Suit.HEARTS));

        revealer.receiveCard(new Card(Rank.EIGHT, Suit.HEARTS));

        // when
        List<Card> revealedCards = revealer.showPartial();
        List<Card> allCards = revealer.show();

        // then
        assertEquals(EXPECTED_REVEALED_CARDS_SECOND_ROUND, revealedCards.size());
        assertEquals(List.of(
            firstRoundCards.get(2),
            secondRoundCards.get(0),
            secondRoundCards.get(1),
            secondRoundCards.get(2)
        ), revealedCards);
        assertEquals(EXPECTED_ALL_CARDS_THIRD_ROUND, allCards.size());
    }
}
