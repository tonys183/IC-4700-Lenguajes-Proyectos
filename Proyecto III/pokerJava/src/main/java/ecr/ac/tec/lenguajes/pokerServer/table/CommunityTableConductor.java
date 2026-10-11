package ecr.ac.tec.lenguajes.pokerServer.table;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.hand.DownHandRevealer;
import ecr.ac.tec.lenguajes.pokerServer.player.BetStatus;
import ecr.ac.tec.lenguajes.pokerServer.player.dealer.CommunityPokerDealer;
import ecr.ac.tec.lenguajes.pokerServer.player.NetworkPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.pot.PotDistribuitor;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public final class CommunityTableConductor implements TableConductor {
    private static final int ROUND_DEAL = 0;
    private static final int ROUND_FLOP = 1;
    private static final int ROUND_TURN = 2;
    private static final int ROUND_RIVER = 3;
    private static final String ROLE_DEALER = "D";
    private static final String ROLE_SMALL_BLIND = "SB";
    private static final String ROLE_BIG_BLIND = "BB";
    private static final String MSG_PLAYER_ROLE = "PLAYER_ROLE:";
    private static final String MSG_ACTIVE_PLAYER = "ACTIVE_PLAYER:";
    private static final String MSG_PLAYER_MONEY = "PLAYER_MONEY:";
    private static final String MSG_PLAYER_ACTION = "PLAYER_ACTION:";
    private static final String WINNER_MESSAGE = "WINNER:";
    private static final String PLAYER_LIST = "PLAYER_LIST";
    private static final String COMMUNITY = "COMMUNITY";
    private static final String OPPONENT_CARD = "OPPONENT_CARD";
    private static final String BACK = "BACK";
    private static final String POT = "POT";
    private static final String START_MONEY = "1000";
    private static final String PLAYER_JOINED = "PLAYER_JOINED";
    private static final String EMPTY = "";
    private static final String ACTION_FOLD = "Fold";
    private static final String ACTION_CALL = "Call";
    private static final String ACTION_RAISE = "Raise";
    private static final String ACTION_ALL_IN = "All In";
    private static final String ACTION_BLIND = "Blind";
    private static final String CURRENCY_SYMBOL = "$";
    private static final String ZERO_MONEY = "$0.0";
    private static final String SEPARATOR = ":";
    private static final String COMMA = ",";
    private static final String HYPHEN = "-";

    private List<PokerPlayer> players;
    private List<PokerPlayer> turnOrder;
    private final List<Card> communityCards;
    private final PotDistribuitor pot;
    private final CommunityPokerDealer dealer;
    private final Map<PokerPlayer, Double> roundBets;
    private int round;
    private boolean allIn;
    private boolean raiseHappened;
    private double currentMaxBet;

    public CommunityTableConductor(final List<Card> communityCards,
                                   final PotDistribuitor pot,
                                   final CommunityPokerDealer dealer) {
        this.communityCards = communityCards;
        this.pot = pot;
        this.dealer = dealer;
        this.round = 0;
        this.allIn = false;
        this.raiseHappened = false;
        this.roundBets = new HashMap<>();
    }

    @Override
    public void reset() {
        this.round = 0;
        communityCards.clear();
        pot.reset();
        dealer.reset();
        roundBets.clear();
        for (PokerPlayer player : players) {
            player.setDealer(false);
            player.setSmallBlind(false);
            player.setBigBlind(false);
        }
        this.allIn = false;
        this.raiseHappened = false;
    }

    @Override
    public void loadPlayers(final List<PokerPlayer> players) {
        this.players = new LinkedList<>(players);
        this.turnOrder = new LinkedList<>(players);
        for (PokerPlayer player : players) {
            player.setActive(true);
            player.initialize(new DownHandRevealer());
        }

        players.getFirst().setSmallBlind(true);
        players.get(1).setBigBlind(true);
        players.getLast().setDealer(true);
        players.getFirst().setTurn(true, false); 
        broadcastActivePlayer(players.getFirst());
        broadcastRoles();
    }

    private void broadcastRoles() {
        for (PokerPlayer player : players) {
            String name = ((NetworkPokerPlayer) player).getPlayerName();
            if (player.isDealer())
                broadcastRole(name, ROLE_DEALER);
            if (player.isSmallBlind())
                broadcastRole(name, ROLE_SMALL_BLIND);
            if (player.isBigBlind())
                broadcastRole(name, ROLE_BIG_BLIND);
        }
    }

    private void broadcastRole(String name, String role) {
        for (PokerPlayer player : players) {
            ((NetworkPokerPlayer) player).sendMessage(MSG_PLAYER_ROLE + name + SEPARATOR + role);
        }
    }

    @Override
    public void startRound(double currentMaxBet) {
        this.currentMaxBet = currentMaxBet;
        switch (round) {
            case ROUND_DEAL -> dealer.deal(players);
            case ROUND_FLOP -> dealer.flop(communityCards, players);
            case ROUND_TURN -> dealer.turn(communityCards, players);
            case ROUND_RIVER -> dealer.river(communityCards, players);
        }
        this.turnOrder = new LinkedList<>();
        for (PokerPlayer player : this.players) {
            if (player.show() != null) {
                this.turnOrder.add(player);
            }
        }
        ++this.round;
        this.raiseHappened = false;
        this.roundBets.clear();

        while (!turnOrder.getFirst().isActive()) {
            PokerPlayer skipped = turnOrder.removeFirst();
            turnOrder.addLast(skipped);
        }
        PokerPlayer nextPlayer = turnOrder.getFirst();
        
        boolean canFold = true;
        if (round == ROUND_DEAL + 1 && !raiseHappened) {
             if (nextPlayer.isSmallBlind() || nextPlayer.isBigBlind()) {
                canFold = false;
            }
        }
        
        double callAmount = Math.max(0, currentMaxBet - roundBets.getOrDefault(nextPlayer, 0.0));
        nextPlayer.setTurn(true, canFold, callAmount);
        broadcastActivePlayer(nextPlayer);
    }

    @Override
    public void startBet(final BetStatus status, final Optional<Double> amount, double currentMaxBet) {
        this.currentMaxBet = currentMaxBet;
        PokerPlayer player = turnOrder.getFirst();
        if (status == BetStatus.FOLD || amount.isEmpty()) {
            player.fold();
            broadcastAction(player, ACTION_FOLD, EMPTY);
        } else if (status == BetStatus.CHECK || !player.isActive() || player.getTotalAmount() == 0) {
            player.check();
            broadcastAction(player, ACTION_CALL, ZERO_MONEY);
        } else if (status == BetStatus.CALL || status == BetStatus.RAISE) {
            player.callOrRaise(amount.get());
            pot.registerBet(player, amount.get());
            
            double currentBet = roundBets.getOrDefault(player, 0.0);
            roundBets.put(player, currentBet + amount.get());
            
            if (status == BetStatus.RAISE) {
                this.currentMaxBet = amount.get();
                raiseHappened = true;
            }

            broadcastPlayerMoney(player);
            String action = (status == BetStatus.CALL) ? ACTION_CALL : ACTION_RAISE;
            broadcastAction(player, action, CURRENCY_SYMBOL + amount.get());
        } else if (status == BetStatus.ALL_IN) {
            player.callOrRaise(amount.get());
            pot.registerBet(player, amount.get());
            
            double currentBet = roundBets.getOrDefault(player, 0.0);
            roundBets.put(player, currentBet + amount.get());
            
            if (amount.get() > currentMaxBet) {
                this.currentMaxBet = amount.get();
                raiseHappened = true;
            }

            broadcastPlayerMoney(player);
            allIn = true;
            broadcastAction(player, ACTION_ALL_IN, CURRENCY_SYMBOL + amount.get());
        }

        player.setTurn(false);
        turnOrder.removeFirst();
        turnOrder.addLast(player);

        while (!turnOrder.getFirst().isActive()) {
            PokerPlayer skipped = turnOrder.removeFirst();
            turnOrder.addLast(skipped);
        }
        
        PokerPlayer nextPlayer = turnOrder.getFirst();
        boolean canFold = true;
        if (round == 1 && !raiseHappened) {
             if (nextPlayer.isSmallBlind() || nextPlayer.isBigBlind()) {
                canFold = false;
            }
        }
        double callAmount = Math.max(0, currentMaxBet - roundBets.getOrDefault(nextPlayer, 0.0));
        nextPlayer.setTurn(true, canFold, callAmount);
        broadcastActivePlayer(turnOrder.getFirst());
    }

    @Override
    public void addSpectator(PokerPlayer player) {
        this.players.add(player);
        if (player instanceof NetworkPokerPlayer networkPlayer) {
            StringBuilder playerListMsg = new StringBuilder(PLAYER_LIST);
            for (PokerPlayer p : players) {
                 playerListMsg.append(SEPARATOR).append(((NetworkPokerPlayer)p).getPlayerName());
            }
            networkPlayer.sendMessage(playerListMsg.toString());

            for (PokerPlayer p : players) {
                String name = ((NetworkPokerPlayer)p).getPlayerName();
                networkPlayer.sendMessage(MSG_PLAYER_MONEY + name + SEPARATOR + p.getTotalAmount());
            }

            for (PokerPlayer p : players) {
                String name = ((NetworkPokerPlayer)p).getPlayerName();
                if (p.isDealer()) networkPlayer.sendMessage(MSG_PLAYER_ROLE + name + SEPARATOR + ROLE_DEALER);
                if (p.isSmallBlind()) networkPlayer.sendMessage(MSG_PLAYER_ROLE + name + SEPARATOR + ROLE_SMALL_BLIND);
                if (p.isBigBlind()) networkPlayer.sendMessage(MSG_PLAYER_ROLE + name + SEPARATOR + ROLE_BIG_BLIND);
            }

            for (Card c : communityCards) {
                networkPlayer.sendMessage(COMMUNITY + SEPARATOR + c.rank() + SEPARATOR + c.suit());
            }
            
            for (PokerPlayer p : players) {
                if (p != player && p.show() != null && !p.show().show().isEmpty()) {
                    String name = ((NetworkPokerPlayer)p).getPlayerName();
                    int cardCount = p.show().show().size();
                    for (int i = 0; i < cardCount; i++) {
                        networkPlayer.sendMessage(OPPONENT_CARD + SEPARATOR + name + SEPARATOR + BACK + SEPARATOR + EMPTY);
                    }
                }
            }
            networkPlayer.sendMessage(POT + SEPARATOR + pot.getTotalPot());

            if (!turnOrder.isEmpty()) {
                 PokerPlayer active = turnOrder.getFirst();
                 networkPlayer.sendMessage(MSG_ACTIVE_PLAYER + ((NetworkPokerPlayer)active).getPlayerName());
            }
            
            String newPlayerName = networkPlayer.getPlayerName();
            String joinMsg = PLAYER_JOINED + SEPARATOR + newPlayerName + SEPARATOR + START_MONEY;
            for (PokerPlayer p : players) {
                if (p != player) {
                    ((NetworkPokerPlayer)p).sendMessage(joinMsg);
                }
            }
        }
    }

    private void broadcastActivePlayer(PokerPlayer activePlayer) {
        NetworkPokerPlayer networkPlayer = (NetworkPokerPlayer) activePlayer;
        String name = networkPlayer.getPlayerName();
        for (PokerPlayer player : players) {
            ((NetworkPokerPlayer) player).sendMessage(MSG_ACTIVE_PLAYER + name);
        }
    }

    private void broadcastPlayerMoney(PokerPlayer player) {
        String name = ((NetworkPokerPlayer) player).getPlayerName();
        String amount = String.valueOf(player.getTotalAmount());
        for (PokerPlayer networkPlayer : players) {
            ((NetworkPokerPlayer) networkPlayer).sendMessage(MSG_PLAYER_MONEY + name + SEPARATOR + amount);
        }
    }

    private void broadcastAction(PokerPlayer player, String action, String amount) {
        String name = ((NetworkPokerPlayer) player).getPlayerName();
        String message = MSG_PLAYER_ACTION + name + SEPARATOR + action + SEPARATOR + amount;
        for (PokerPlayer networkPlayer : players) {
            ((NetworkPokerPlayer) networkPlayer).sendMessage(message);
        }
    }

    @Override
    public void endGame(final List<PokerPlayer> winners) {
        pot.createPots();
        pot.distribute(winners);
        dealer.reset();
        for (PokerPlayer player : turnOrder) {
            broadcastPlayerMoney(player);
        }
        
        for (PokerPlayer winner : winners) {
            String winnerName = ((NetworkPokerPlayer) winner).getPlayerName();
            List<Card> winnerHand = winner.show().show();
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(WINNER_MESSAGE).append(winnerName).append(SEPARATOR);
            
            StringBuilder winnerCardsStr = new StringBuilder();
            for (Card card : winnerHand) {
                if (!winnerCardsStr.isEmpty()) {
                    winnerCardsStr.append(COMMA);
                }
                winnerCardsStr.append(card.rank()).append(HYPHEN).append(card.suit());
            }
            messageBuilder.append(winnerCardsStr);

            for (PokerPlayer player : turnOrder) {
                String playerName = ((NetworkPokerPlayer) player).getPlayerName();
                if (playerName.equals(winnerName)) {
                    continue;
                }
                List<Card> playerHand = player.show().show();
                StringBuilder playerCardsStr = new StringBuilder();
                for (Card card : playerHand) {
                    if (!playerCardsStr.isEmpty()) {
                        playerCardsStr.append(COMMA);
                    }
                    playerCardsStr.append(card.rank()).append(HYPHEN).append(card.suit());
                }
                messageBuilder.append(SEPARATOR).append(playerName).append(SEPARATOR).append(playerCardsStr);
            }

            String message = messageBuilder.toString();
            for (PokerPlayer player : players) {
                ((NetworkPokerPlayer) player).sendMessage(message);
            }
        }
    }

    @Override
    public double getPotAmount() {
        return pot.getTotalPot();
    }

    @Override
    public void dealAll() {
        if (round <= ROUND_DEAL) {
            dealer.deal(players);
        }
        if (round <= ROUND_FLOP) {
            dealer.flop(communityCards, players);
        }
        if (round <= ROUND_TURN) {
            dealer.turn(communityCards, players);
        }
        if (round <= ROUND_RIVER) {
            dealer.river(communityCards, players);
        }
    }
}
