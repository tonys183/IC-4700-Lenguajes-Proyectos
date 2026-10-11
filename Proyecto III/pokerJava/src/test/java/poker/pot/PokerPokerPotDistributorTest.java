package poker.pot;

import ecr.ac.tec.lenguajes.pokerServer.pot.PokerPot;
import ecr.ac.tec.lenguajes.pokerServer.pot.PokerPotDistributor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ecr.ac.tec.lenguajes.pokerServer.player.DefaultPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PokerPokerPotDistributorTest {
    private static final int EXPECTED_BET_PLAYER1 = 100;
    private static final int EXPECTED_BET_PLAYER2 = 200;
    private static final int EXPECTED_CONTRIBUTORS = 2;
    private static final int EXPECTED_POT_OWNERS_SAME_BET = 1;
    private static final int EXPECTED_POT_OWNERS = 2;
    private static final double EXPECTED_DISTRIBUTION_PLAYER1 = 750;
    private static final double EXPECTED_DISTRIBUTION_PLAYER2 = 0;
    private static final double EXPECTED_DISTRIBUTION_PLAYER3 = 1750;
    private static final double EXPECTED_DISTRIBUTION_SAME_BET_PLAYER_1 = 3000;
    private static final double EXPECTED_DISTRIBUTION_SAME_BET_OTHER = 0;
    private static final double ERROR_MARGIN = 0.001;

    private static PokerPotDistributor distributor;
    private static Map<PokerPlayer, Double> contributions;
    private static Map<PokerPot, List<PokerPlayer>> potOwners;

    @BeforeEach
    public void setup() {
        contributions = new HashMap<>();
        potOwners = new HashMap<>();
        distributor = new PokerPotDistributor(contributions, potOwners);
    }

    @Test
    public void registerBetTest(){
        // given
        PokerPlayer player1 = new DefaultPokerPlayer();
        PokerPlayer player2 = new DefaultPokerPlayer();

        // when
        distributor.registerBet(player1, 100);
        distributor.registerBet(player2, 200);

        // then
        assertEquals(EXPECTED_CONTRIBUTORS, contributions.size());
        assertEquals(EXPECTED_BET_PLAYER1, contributions.get(player1));
        assertEquals(EXPECTED_BET_PLAYER2, contributions.get(player2));
    }

    @Test
    public void createPotTest(){
        // given
        PokerPlayer player1 = new DefaultPokerPlayer();
        PokerPlayer player2 = new DefaultPokerPlayer();
        PokerPlayer player3 = new DefaultPokerPlayer();

        // when
        distributor.registerBet(player1, 500);
        distributor.registerBet(player2, 1000);
        distributor.registerBet(player3, 2000);
        distributor.createPots();

        // then
        assertEquals(EXPECTED_POT_OWNERS, potOwners.size());
    }

    @Test
    public void  distributeTest(){
        // given
        PokerPlayer player1 = new DefaultPokerPlayer();
        PokerPlayer player2 = new DefaultPokerPlayer();
        PokerPlayer player3 = new DefaultPokerPlayer();

        distributor.registerBet(player1, 500);
        distributor.registerBet(player2, 1000);
        distributor.registerBet(player3, 2000);

        // when
        distributor.createPots();
        List<PokerPlayer> winners = List.of(player1, player3);
        distributor.distribute(winners);

        // then
        assertEquals(EXPECTED_DISTRIBUTION_PLAYER1, player1.getTotalAmount(), ERROR_MARGIN);
        assertEquals(EXPECTED_DISTRIBUTION_PLAYER2, player2.getTotalAmount(), ERROR_MARGIN);
        assertEquals(EXPECTED_DISTRIBUTION_PLAYER3, player3.getTotalAmount(), ERROR_MARGIN);
    }

    @Test
    public void sameBetAmountTest(){
        // given
        PokerPlayer player1 = new DefaultPokerPlayer();
        PokerPlayer player2 = new DefaultPokerPlayer();
        PokerPlayer player3 = new DefaultPokerPlayer();

        distributor.registerBet(player1, 1000);
        distributor.registerBet(player2, 1000);
        distributor.registerBet(player3, 1000);

        // when
        distributor.createPots();
        assertEquals(EXPECTED_POT_OWNERS_SAME_BET, potOwners.size());
        List<PokerPlayer> winners = List.of(player1);
        distributor.distribute(winners);

        // then
        assertEquals(EXPECTED_DISTRIBUTION_SAME_BET_PLAYER_1, player1.getTotalAmount(), ERROR_MARGIN);
        assertEquals(EXPECTED_DISTRIBUTION_SAME_BET_OTHER, player2.getTotalAmount(), ERROR_MARGIN);
        assertEquals(EXPECTED_DISTRIBUTION_SAME_BET_OTHER, player3.getTotalAmount(), ERROR_MARGIN);
    }

    @Test
    public void sameBetAmountAllRoundsTest(){
        // given
        PokerPlayer player1 = new DefaultPokerPlayer();
        PokerPlayer player2 = new DefaultPokerPlayer();
        PokerPlayer player3 = new DefaultPokerPlayer();

        distributor.registerBet(player1, 100);
        distributor.registerBet(player2, 100);
        distributor.registerBet(player3, 100);

        distributor.registerBet(player1, 100);
        distributor.registerBet(player2, 100);
        distributor.registerBet(player3, 100);

        distributor.registerBet(player1, 100);
        distributor.registerBet(player2, 100);
        distributor.registerBet(player3, 100);

        distributor.registerBet(player1, 100);
        distributor.registerBet(player2, 100);
        distributor.registerBet(player3, 100);
        distributor.createPots();

        // when
        assertEquals(1, potOwners.size());

        List<PokerPlayer> winners = List.of(player1);
        distributor.distribute(winners);

        // then
        assertEquals(1200, player1.getTotalAmount(), ERROR_MARGIN);
        assertEquals(0, player2.getTotalAmount(), ERROR_MARGIN);
        assertEquals(0, player3.getTotalAmount(), ERROR_MARGIN);
    }
}
