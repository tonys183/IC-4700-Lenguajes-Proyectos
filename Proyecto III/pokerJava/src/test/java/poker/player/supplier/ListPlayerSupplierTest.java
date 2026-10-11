package poker.player.supplier;

import ecr.ac.tec.lenguajes.pokerServer.player.DefaultPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.supplier.ListPlayerSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class ListPlayerSupplierTest {
    private static List<PokerPlayer> players;
    private static PokerPlayer player1;
    private static PokerPlayer player2;
    private static PokerPlayer player3;
    private static PokerPlayer player4;
    private static ListPlayerSupplier supplier;

    @BeforeEach
    public void setup() {
        players = new ArrayList<>();
        player1 = new DefaultPokerPlayer();
        player2 = new DefaultPokerPlayer();
        player3 = new DefaultPokerPlayer();
        player4 = new DefaultPokerPlayer();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        player1.setSmallBlind(true);
        player2.setBigBlind(true);
        player4.setDealer(true);
        supplier = new ListPlayerSupplier(players);

    }

    @Test
    public void syncAndSupplyTest(){
        // given
        PokerPlayer player5 = new DefaultPokerPlayer();
        PokerPlayer player6 = new DefaultPokerPlayer();

        // when
            supplier.add(player5);
            supplier.add(player6);
            Collections.rotate(players, 1);
        //then
            assertEquals(6, supplier.supplyAndSync().size());
            assertEquals(player4, players.getFirst());
            assertEquals(player1, players.get(1));
            assertEquals(player5, players.get(2));
            assertEquals(player6, players.get(3));
            assertEquals(player2, players.get(4));
            assertEquals(player3, players.get(5));

    }
}
