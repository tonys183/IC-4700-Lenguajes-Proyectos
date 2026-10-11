package poker.game.managers;
import ecr.ac.tec.lenguajes.pokerServer.config.DefaultConfig;
import ecr.ac.tec.lenguajes.pokerServer.game.managers.GameManager;
import ecr.ac.tec.lenguajes.pokerServer.game.GameType;
import ecr.ac.tec.lenguajes.pokerServer.player.BetStatus;
import ecr.ac.tec.lenguajes.pokerServer.player.DefaultPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


public final class MapGameManagerTest {
    private GameManager gameManager;
    private DefaultConfig config;
    private List<PokerPlayer> players;
    private PokerPlayer player1;
    private PokerPlayer player2;
    private PokerPlayer player3;

    @BeforeEach
    public void setup(){
        player1 = new DefaultPokerPlayer();
        player2 = new DefaultPokerPlayer();
        player3 = new DefaultPokerPlayer();
        players = new ArrayList<>();
        players.addAll(List.of(player1, player2, player3));
        config = new DefaultConfig(players);
        config.initialize();
        gameManager = config.getGameManager();
    }

    @Test
    public void holdemTestNormalRound(){
        // given
        int rounds = 4;
        int betRounds = -1;
        boolean finishedGame = false, finishedRound = false;
        player1.addAmount(200);
        player2.addAmount(200);
        player3.addAmount(200);

        // when

        while(!finishedGame){
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                betRounds = 3;
                finishedRound = false;
                --rounds;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
                --betRounds;
            }

        }
        gameManager.finishCurrentGame(List.of(player1));

        // then

        assertFalse(player3.isDealer());
        assertFalse(player1.isSmallBlind());
        assertFalse(player2.isBigBlind());
        assertTrue(player2.isDealer());
        assertTrue(player3.isSmallBlind());
        assertTrue(player1.isBigBlind());
        assertEquals(GameType.OMAHA, gameManager.getCurrentGame());
        assertEquals(0, rounds);
        assertEquals(0, betRounds);
        assertEquals(600.0 ,player1.getTotalAmount());

    }

    @Test
    public void holdemAndOmahaTestAddPlayerRound(){
        // given

        boolean finishedGame = false, finishedRound = false;
        player3.addAmount(200);
        player2.addAmount(200);
        player1.addAmount(200);
        PokerPlayer player4 = new DefaultPokerPlayer();
        gameManager.addPlayer(player4);
        while(!finishedGame){
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                finishedRound = false;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
            }
        }
        gameManager.finishCurrentGame(List.of(player1));

        // when

        assertEquals(600.0 ,player1.getTotalAmount());
        assertTrue(player2.isDealer());
        assertTrue(player3.isSmallBlind());
        assertTrue(player1.isBigBlind());
        

        int rounds = 4;
        int betRounds = -1;
        finishedGame = finishedRound = false;
        player4.addAmount(200);
        player3.addAmount(200);
        player2.addAmount(200);
        player1.addAmount(200);

        gameManager.addPlayer(player4);
        while(!finishedGame){
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                betRounds = 4;
                finishedRound = false;
                --rounds;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
                --betRounds;

            }
        }
        gameManager.finishCurrentGame(List.of(player4));


        // then

        assertEquals(GameType.RAZZ, gameManager.getCurrentGame());
        assertEquals(0, rounds);
        assertEquals(0, betRounds);
        assertEquals(800.0 ,player4.getTotalAmount());

    }

    @Test
    public void holdemAndOmahaTestRemovePlayerRound(){
        // given

        boolean finishedGame = false, finishedRound = false, delete = false;
        player3.addAmount(200);
        player2.addAmount(200);
        player1.addAmount(200);
        PokerPlayer player4 = new DefaultPokerPlayer();
        gameManager.addPlayer(player4);

        while(!finishedGame){
            if (!delete){
                delete = true;
                gameManager.removePlayer(player3);

            }
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                finishedRound = false;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
            }

        }
        gameManager.finishCurrentGame(List.of(player1));

        // when

        assertEquals(400.0 ,player1.getTotalAmount());
        assertEquals(3, players.size());
        assertTrue(player4.isDealer());
        assertFalse(player3.isActive());
        assertTrue(player2.isBigBlind());
        assertTrue(player1.isSmallBlind());


        int rounds = 4;
        int betRounds = -1;
        finishedGame = finishedRound = false;
        player4.addAmount(200);
        player2.addAmount(200);
        while(!finishedGame){
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                betRounds = 3;
                finishedRound = false;
                --rounds;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
                --betRounds;
            }
        }

        gameManager.finishCurrentGame(List.of(player4));


        // then
        assertTrue(player4.isSmallBlind());
        assertTrue(player2.isDealer());
        assertTrue(player1.isBigBlind());
        assertEquals(3, players.size());
        assertEquals(GameType.RAZZ, gameManager.getCurrentGame());
        assertEquals(0, rounds);
        assertEquals(0, betRounds);
        assertEquals(600.0 ,player4.getTotalAmount());

    }

    @Test
    public void holdemOmahaandRazzTest(){
        // given

        boolean finishedGame = false, finishedRound = false, delete = false;
        player3.addAmount(200);
        player2.addAmount(200);
        player1.addAmount(200);
        PokerPlayer player4 = new DefaultPokerPlayer();
        gameManager.addPlayer(player4);

        while(!finishedGame){
            if (!delete){
                delete = true;
                gameManager.removePlayer(player3);

            }
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                finishedRound = false;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
            }

        }
        gameManager.finishCurrentGame(List.of(player1));
        PokerPlayer player5 = new DefaultPokerPlayer();
        player4.addAmount(200);
        player2.addAmount(200);
        gameManager.addPlayer(player5);
        finishedGame = finishedRound = false;
        while(!finishedGame){
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                finishedRound = false;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
            }
        }
        gameManager.finishCurrentGame(List.of(player4));

        // when
        int betRounds = -1;
        int rounds = 5;
        finishedGame = finishedRound = false;
        player2.addAmount(250);
        player5.addAmount(250);
        player1.addAmount(50);
        while(!finishedGame){
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                betRounds = 4;
                finishedRound = false;
                --rounds;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
                --betRounds;
            }
        }
        gameManager.finishCurrentGame(List.of(player2, player5));
        // then
        assertTrue(player4.isBigBlind());
        assertTrue(player2.isSmallBlind());
        assertTrue(player5.isDealer());
        assertEquals(4, players.size());
        assertEquals(GameType.SEVEN_CARD_STUD, gameManager.getCurrentGame());
        assertEquals(0, rounds);
        assertEquals(0, betRounds);
        assertEquals(500.0 ,player2.getTotalAmount());
        assertEquals(500.0 ,player5.getTotalAmount());
    }

    @Test
    public void holdemOmahaRazzSevenTest(){
        // given

        boolean finishedGame = false, finishedRound = false, delete = false;
        player3.addAmount(200);
        player2.addAmount(200);
        player1.addAmount(200);
        PokerPlayer player4 = new DefaultPokerPlayer();
        PokerPlayer player5 = new DefaultPokerPlayer();
        gameManager.addPlayer(player4);
        gameManager.addPlayer(player5);

        while(!finishedGame){
            if (!delete){
                delete = true;
                gameManager.removePlayer(player3);

            }
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                finishedRound = false;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
            }

        }
        gameManager.finishCurrentGame(List.of(player1));
        assertEquals(4, players.size());

        player4.addAmount(200);
        player2.addAmount(200);
        player5.addAmount(200);
        finishedGame = finishedRound = false;
        while(!finishedGame){
            finishedGame = gameManager.doCurrentRound();
            if (!finishedGame) {
                finishedRound = false;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
            }
        }
        gameManager.finishCurrentGame(List.of(player4));

        // when
        int betRounds = -1;
        int rounds = 5;
        finishedGame = finishedRound = delete = false;
        player2.addAmount(250);
        player5.addAmount(250);
        player1.addAmount(50);
        while(!finishedGame){
            finishedGame = gameManager.doCurrentRound();
            if (!delete){
                delete = true;
                gameManager.removePlayer(player1);

            }
            if (!finishedGame) {
                betRounds = 4;
                finishedRound = false;
                --rounds;
            }
            while (!finishedRound && !finishedGame){
                finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
                --betRounds;
            }
        }
        gameManager.finishCurrentGame(List.of(player2, player5));
        // then
        assertEquals(3, players.size());
        assertEquals(GameType.SEVEN_CARD_STUD, gameManager.getCurrentGame());
        assertEquals(0, rounds);
        assertEquals(0, betRounds);
        assertEquals(375.0 ,player2.getTotalAmount());
        assertEquals(375.0 ,player5.getTotalAmount());
    }

    @Test
    public void allGamesTest(){
        // given

        boolean finishedGame = false, finishedRound = false, finishedAllGames = false;
        player3.addAmount(1100);
        player2.addAmount(1100);
        player1.addAmount(1100);
        int games = 5;
        while (!finishedAllGames) {
            while(!finishedGame){
                finishedGame = gameManager.doCurrentRound();
                if (!finishedGame) {
                    finishedRound = false;
                }
                while (!finishedRound && !finishedGame){
                    finishedRound = gameManager.playerBet(BetStatus.RAISE, Optional.of(50.0));
                }
            }
            finishedGame = finishedRound = false;
            finishedAllGames = gameManager.finishCurrentGame(List.of(player1));
            --games;
        }
        assertEquals(0,games);
        assertEquals(3300, player1.getTotalAmount());
    }


}
