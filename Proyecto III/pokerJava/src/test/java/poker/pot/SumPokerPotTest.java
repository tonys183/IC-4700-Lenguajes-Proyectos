package poker.pot;

import ecr.ac.tec.lenguajes.pokerServer.pot.PokerPot;
import ecr.ac.tec.lenguajes.pokerServer.pot.SumPokerPot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SumPokerPotTest {
    private static PokerPot pot;
    private static final double EXPECTED_HAND_OVER = 300;

    @BeforeEach
    public void setup() {
         pot = new SumPokerPot();
    }

    @Test
    public void handOverTest() {
        //given

        // when
        double amount = pot.handOver();

        // then
        assertEquals(0, amount);
    }

    @Test
    public void addTest() {
        // given

        // when
        pot.add(100);
        pot.add(200);

        // then
        assertEquals(EXPECTED_HAND_OVER, pot.handOver());
    }
}
