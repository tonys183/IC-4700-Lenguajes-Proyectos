package ecr.ac.tec.lenguajes.pokerServer.card;

public record Card(Rank rank, Suit suit) {
    @Override
    public boolean equals(Object o) {
        Card other = (Card) o;
        return this.rank == other.rank && this.suit == other.suit;
    }
}
