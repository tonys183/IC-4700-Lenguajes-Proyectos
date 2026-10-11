package ecr.ac.tec.lenguajes.pokerServer.server.c;

import ecr.ac.tec.lenguajes.pokerServer.card.Card;
import ecr.ac.tec.lenguajes.pokerServer.game.GameType;
import ecr.ac.tec.lenguajes.pokerServer.player.NetworkPokerPlayer;
import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.ArrayList;
import java.util.List;

public final class ConsoleCComunicator implements CComunicator {
    private static final String HI_WINNERS_SEPARATOR = "WINNERS_HI:";
    private static final String LOW_WINNERS_SEPARATOR = "WINNERS_LOW:";
    private static final String LINES_SEPARATOR = "\\R";
    private record WinnersBlock(List<String> hiWinners, List<String> lowWinners) {}

    @Override
    public String buildMessage(List<PokerPlayer> players, GameType gameType) {
        return parsePlayers(players, gameType);
    }

    @Override
    public List<PokerPlayer> parseWinners(String winnersRaw, List<PokerPlayer> players, GameType gameType) {
        System.out.println(winnersRaw);
        WinnersBlock winners = parseWinnerBlocks(winnersRaw);
        if (GameType.RAZZ == gameType) {
            return matchPlayers(winners.lowWinners(), new ArrayList<>(players));
        }

        if (GameType.EIGHT_OR_BETTER == gameType) {
            List<PokerPlayer> hiLowWinners = new ArrayList<>(matchPlayers(winners.hiWinners(), new ArrayList<>(players)));
            hiLowWinners.addAll(matchPlayers(winners.lowWinners(), new ArrayList<>(players)));
            return hiLowWinners;
        }
        return matchPlayers(winners.hiWinners, new ArrayList<>(players));

    }

    private WinnersBlock parseWinnerBlocks(String winnersRaw) {
        List<String> hiWinners = new ArrayList<>();
        List<String> lowWinners = new ArrayList<>();
        String[] lines = winnersRaw.split(LINES_SEPARATOR);
        boolean found = false, hi = false;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.equalsIgnoreCase(HI_WINNERS_SEPARATOR)) {
                found = hi = true;
                continue;
            }
            if (line.equalsIgnoreCase(LOW_WINNERS_SEPARATOR)) {
                hi = false;
                continue;
            }
            if (found && hi) {
                hiWinners.add(line);
            } else if (found) {
                lowWinners.add(line);
            }
        }

        return new WinnersBlock(hiWinners, lowWinners);
    }


    private List<PokerPlayer> matchPlayers(List<String> winnerNames, List<PokerPlayer> players) {
        return players.stream()
                .filter(player -> winnerNames.stream().anyMatch(name ->
                name.equalsIgnoreCase(((NetworkPokerPlayer) player).getPlayerName()))).toList();
    }


    private String parsePlayers(List<PokerPlayer> players, GameType gameType) {
        StringBuilder parsedNames = new StringBuilder();
        parsedNames.append(gameType.toString()).append("\n");
        for (PokerPlayer pokerPlayer : players ) {
            parsedNames
                .append(((NetworkPokerPlayer) pokerPlayer).getPlayerName())
                .append(": ")
                .append(parseCards(pokerPlayer.show().show()))
                .append('\n');
        }

        PokerPlayer pokerPlayer = players.getFirst();

        parsedNames
            .append('\n')
            .append("COMUNITY_CARDS: ")
            .append(parseCards(pokerPlayer.getCommunityCards()))
            .append('\n');

        return parsedNames.toString();
    }

    private String parseCards(List<Card> cards) {
        StringBuilder parsedCards = new StringBuilder();
        for (Card card : cards ) {
            parsedCards
                .append(card.suit().toString().charAt(0))
                .append(',')
                .append(card.rank().ordinal())
                .append(' ');
        }
        return parsedCards.toString();
    }
}
