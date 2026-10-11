package poker.table;

import ecr.ac.tec.lenguajes.pokerServer.table.CommunityTableConductor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ecr.ac.tec.lenguajes.pokerServer.card.Suit;
import ecr.ac.tec.lenguajes.pokerServer.card.Rank;
import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.card.DeckHandler;
import ecr.ac.tec.lenguajes.pokerServer.card.StackDeckHandler;
import ecr.ac.tec.lenguajes.pokerServer.hand.DownHandRevealer;
import ecr.ac.tec.lenguajes.pokerServer.player.BetStatus;
import ecr.ac.tec.lenguajes.pokerServer.player.DefaultPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.CommunityPokerDealer;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.DefaultCommunityPokerDealer;
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

public final class HoldTableConductorTest {
    private CommunityTableConductor conductor;
    private List<PokerPlayer> players;
    private List<Card> communityCards;
    private PotDistribuitor pot;
    private CommunityPokerDealer dealer;
    private Map<PokerPlayer, Double> contributions;
    private Map<PokerPot, List<PokerPlayer>> potOwners;
    private DeckHandler deck = new StackDeckHandler();

    private DefaultPokerPlayer player1;
    private DefaultPokerPlayer player2;
    private DefaultPokerPlayer player3;

    private static final int CARDS_PER_PLAYER = 2;
    private static final int FLOP_COMMUNITY_CARDS = 3;
    private static final int TURN_COMMUNITY_CARDS = 4;
    private static final int RIVER_COMMUNITY_CARDS = 5;

    private static final double INITIAL_AMOUNT = 50.0;
    private static final double SECOND_ROUND_TOTAL = 100.0;
    private static final double THIRD_ROUND_TOTAL = 150.0;
    private static final double EXTRA_STACK_AMOUNT = 200.0;
    private static final double FINAL_POT_AMOUNT = 600.0;

    @BeforeEach
    public void setup() {
        players = new LinkedList<>();

        player1 = new DefaultPokerPlayer();
        player2 = new DefaultPokerPlayer();
        player3 = new DefaultPokerPlayer();

        player1.initialize(new DownHandRevealer());
        player2.initialize(new DownHandRevealer());
        player3.initialize(new DownHandRevealer());

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
        dealer = new DefaultCommunityPokerDealer(CARDS_PER_PLAYER, deck);
        communityCards = new LinkedList<>();
        pot = new PokerPotDistributor(contributions, potOwners);
        conductor = new CommunityTableConductor(communityCards, pot, dealer);
        conductor.loadPlayers(players);
    }

    @Test
    public void resetTest() {
        // given
        communityCards.add(new Card(Rank.ACE, Suit.CLUBS));
        communityCards.add(new Card(Rank.ACE, Suit.DIAMONDS));
        communityCards.add(new Card(Rank.ACE, Suit.SPADES));
        communityCards.add(new Card(Rank.ACE, Suit.HEARTS));

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
        assertTrue(communityCards.isEmpty());
        assertTrue(contributions.isEmpty());
    }

    @Test
    public void roundOneTest() {
        // given
        conductor.startRound();

        // when
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));
        conductor.startBet(BetStatus.CALL, Optional.of(INITIAL_AMOUNT));

        // then
        assertTrue(communityCards.isEmpty());
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_PLAYER));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == INITIAL_AMOUNT));
    }

    @Test
    public void roundSecondTest() {
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
        assertEquals(FLOP_COMMUNITY_CARDS, communityCards.size());
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_PLAYER));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == SECOND_ROUND_TOTAL));
    }

    @Test
    public void roundThirdTest() {
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
        assertEquals(TURN_COMMUNITY_CARDS, communityCards.size());
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_PLAYER));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == THIRD_ROUND_TOTAL));
    }

    @Test
    public void roundFinalTest() {
        // given
        player1.addAmount(EXTRA_STACK_AMOUNT);
        player2.addAmount(EXTRA_STACK_AMOUNT);
        player3.addAmount(EXTRA_STACK_AMOUNT);

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
        assertEquals(RIVER_COMMUNITY_CARDS, communityCards.size());
        assertTrue(players.stream().allMatch(player -> player.show().show().size() == CARDS_PER_PLAYER));
        assertTrue(contributions.values().stream().allMatch(amount -> amount == EXTRA_STACK_AMOUNT));
        assertEquals(FINAL_POT_AMOUNT, contributions.values().stream().mapToDouble(Double::doubleValue).sum());
        assertEquals(FINAL_POT_AMOUNT, player1.getTotalAmount());
    }
}
