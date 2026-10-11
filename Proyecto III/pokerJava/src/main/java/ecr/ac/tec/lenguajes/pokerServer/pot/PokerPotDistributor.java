package ecr.ac.tec.lenguajes.pokerServer.pot;

import ecr.ac.tec.lenguajes.pokerServer.player.PokerPlayer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public final class PokerPotDistributor implements PotDistribuitor {
    private final Map<PokerPlayer, Double> contributions;
    private final Map<PokerPot, List<PokerPlayer>> potOwners;

    public PokerPotDistributor(final Map<PokerPlayer, Double> contributions,
            final Map<PokerPot, List<PokerPlayer>> potOwners) {
        this.contributions = contributions;
        this.potOwners = potOwners;
    }

    @Override
    public void registerBet(PokerPlayer player, double amount) {
        contributions.merge(player, amount, Double::sum);
    }

    @Override
    public void createPots() {
        List<Map.Entry<PokerPlayer, Double>> sortedPot = new ArrayList<>(contributions.entrySet());
        sortedPot.sort(Comparator.comparingDouble(Map.Entry::getValue));

        double previousBetPlayer = 0;
        Set<PokerPlayer> remainingPlayers = new HashSet<>(contributions.keySet());

        for (Map.Entry<PokerPlayer, Double> contribution : sortedPot) {
            double difference = contribution.getValue() - previousBetPlayer;
            if (difference > 0 && !remainingPlayers.isEmpty()) {
                PokerPot pot = new SumPokerPot();
                pot.add(difference * remainingPlayers.size());
                potOwners.put(pot, new ArrayList<>(remainingPlayers));
                previousBetPlayer = contribution.getValue();
            }
            remainingPlayers.remove(contribution.getKey());
        }
    }

    @Override
    public void distribute(List<PokerPlayer> winners) {
        for (Map.Entry<PokerPot, List<PokerPlayer>> entry : potOwners.entrySet()) {
            PokerPot pot = entry.getKey();
            List<PokerPlayer> owners = entry.getValue();
            int totalWinnersInPot = Math.toIntExact(winners.stream().filter(owners::contains).count());
            for (PokerPlayer player : winners) {
                if (owners.contains(player)) {
                    final double share = pot.handOver() / totalWinnersInPot;
                    player.addAmount(share);
                }
            }
        }
    }

    @Override
    public void reset() {
        contributions.clear();
        potOwners.clear();
    }

    @Override
    public double getTotalPot() {
        return contributions.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    public double getPlayerBet(PokerPlayer player) {
        return contributions.getOrDefault(player, 0.0);
    }
}
