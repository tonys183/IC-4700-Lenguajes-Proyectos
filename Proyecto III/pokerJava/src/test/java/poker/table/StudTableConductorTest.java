package poker.table;

import ecr.ac.tec.lenguajes.pokerServer.table.StudTableConductor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.card.DeckHandler;
import ecr.ac.tec.lenguajes.pokerServer.card.StackDeckHandler;
import ecr.ac.tec.lenguajes.pokerServer.hand.UpDownHandPartialRevealer;
import ecr.ac.tec.lenguajes.pokerServer.player.BetStatus;
import ecr.ac.tec.lenguajes.pokerServer.player.DefaultPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.PokerDealer;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.StudPokerDealer;
import ecr.ac.tec.lenguajes.pokerServer.pot.PokerPot;
import ecr.ac.tec.lenguajes.pokerServer.pot.PokerPotDistributor;
import ecr.ac.tec.lenguajes.pokerServer.pot.PotDistribuitor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public final class StudTableConductorTest {
    private StudTableConductor conductor;
    private List<PokerPlayer> players;
    private List<Card> communityCards;
    private PotDistribuitor pot;
    private PokerDealer dealer;
    private Map<PokerPlayer, Double> contributions;
    private Map<PokerPot, List<PokerPlayer>> potOwners;
    private DeckHandler deck = new StackDeckHandler();


    private DefaultPokerPlayer player1;
    private DefaultPokerPlayer player2;
    private DefaultPokerPlayer player3;

    private static final int CARDS_PER_THIRD_STREET = 3;
    private static final int CARDS_PER_FOURTH_STREET = 4;
    private static final int CARDS_PER_FIFTH_STREET = 5;
    private static final int CARDS_PER_SIXTH_STREET = 6;
    private static final int CARDS_PER_SEVENTH_STREET = 7;

    private static final double INITIAL_AMOUNT = 50.0;
    private static final double SECOND_ROUND_TOTAL = 100.0;
    private static final double THIRD_ROUND_TOTAL = 150.0;
    private static final double FOURTH_ROUND_TOTAL = 200.0;
    private static final double FIFTH_ROUND_TOTAL = 250.0;
    private static final double FINAL_POT_AMOUNT = 750.0;

    @BeforeEach
    public void setup() {
        players = new LinkedList<>();

        player1 = new DefaultPokerPlayer();
        player2 = new DefaultPokerPlayer();
        player3 = new DefaultPokerPlayer();

        player1.initialize(new UpDownHandPartialRevealer());
        player2.initialize(new UpDownHandPartialRevealer());
        player3.initialize(new UpDownHandPartialRevealer());

        players.add(player1);
        players.add(player2);
        players.add(player3);
        player1.setSmallBlind(true);
        player2.setBigBlind(true);
        player3.setDealer(true);

        contributions = new HashMap<>();
        potOwners = new HashMap<>();

        deck.fill();
        deck.shuffle();
        dealer = new StudPokerDealer(deck);
        pot = new PokerPotDistributor(contributions, potOwners);
        conductor = new StudTableConductor(pot, dealer);
        conductor.loadPlayers(players);
    }

    @Test
    public void resetTest() {
        // given
        pot.registerBet(players.getFirst(), INITIAL_AMOUNT);
        pot.registerBet(players.get(1), INITIAL_AMOUNT);

        // when
        PokerPlayer oldSmallBlind = players.getFirst();
        PokerPlayer oldBigBlind = players.get(1);
        PokerPlayer oldDealer = players.getLast();
        assertTrue(oldDealer.isDealer());
        assertTrue(oldSmallBlind.isSmallBlind());
        assertTrue(oldBigBlind.isBigBlind());

        conductor.reset();

        // then
        assertFalse(oldDealer.isDealer());
        assertFalse(oldSmallBlind.isSmallBlind());
        assertFalse(oldBigBlind.isBigBlind());
        assertTrue(contributions.isEmpty());
    }

    @Test
    public void roundThirdStreetTest() {
        // given
        conductor.startRound();

        // when
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // then
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_THIRD_STREET));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == INITIAL_AMOUNT));
    }

    @Test
    public void roundFourthStreetTest() {
        // given
        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // when
        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // then
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_FOURTH_STREET));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == SECOND_ROUND_TOTAL));
    }

    @Test
    public void roundFifthStreetTest() {
        // given
        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // when
        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // then
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_FIFTH_STREET));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == THIRD_ROUND_TOTAL));
    }

    @Test
    public void roundSixthStreetTest() {
        // given
        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // when
        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // then
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_SIXTH_STREET));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == FOURTH_ROUND_TOTAL));
    }

    @Test
    public void roundSeventhTest() {
        // given
        player1.addAmount(FIFTH_ROUND_TOTAL);
        player2.addAmount(FIFTH_ROUND_TOTAL);
        player3.addAmount(FIFTH_ROUND_TOTAL);

        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        conductor.startRound();
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // when
        conductor.endGame(List.of(player1));

        // then
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_SEVENTH_STREET));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == FIFTH_ROUND_TOTAL));
        assertEquals(FINAL_POT_AMOUNT, contributions.values().stream().mapToDouble(Double::doubleValue).sum());
        assertEquals(FINAL_POT_AMOUNT, player1.getTotalAmount());
    }
}
