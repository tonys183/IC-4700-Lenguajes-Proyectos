package ecr.ac.tec.lenguajes.pokerServer.pot;

public final class SumPokerPot implements PokerPot {
    private double potAmount = 0;

    @Override
    public double handOver() {
        return this.potAmount;
    }

    @Override
    public void add(double amount) {
        potAmount += amount;
    }
}
