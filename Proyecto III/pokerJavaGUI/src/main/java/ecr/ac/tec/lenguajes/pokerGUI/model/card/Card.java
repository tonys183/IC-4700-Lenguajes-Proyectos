package ecr.ac.tec.lenguajes.pokerGUI.model.card;

public record Card(Rank rank, Suit suit) {
    private static final String RANK_TWO = "2";
    private static final String RANK_THREE = "3";
    private static final String RANK_FOUR = "4";
    private static final String RANK_FIVE = "5";
    private static final String RANK_SIX = "6";
    private static final String RANK_SEVEN = "7";
    private static final String RANK_EIGHT = "8";
    private static final String RANK_NINE = "9";
    private static final String RANK_TEN = "10";
    private static final String IMAGE_EXTENSION = ".png";
    private static final String IMAGE_SEPARATOR = "_of_";
    private static final String RANK_SEPARATOR = " of ";
    private static final String PATH_SEPARATOR = "/";
    private static final String BACK_IMAGE_NAME = "card_back.png";

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Card card = (Card) o;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public String toString() {
        return rank + RANK_SEPARATOR + suit;
    }

    public String getImageName() {
        String rankStr = switch (rank) {
            case TWO -> RANK_TWO;
            case THREE -> RANK_THREE;
            case FOUR -> RANK_FOUR;
            case FIVE -> RANK_FIVE;
            case SIX -> RANK_SIX;
            case SEVEN -> RANK_SEVEN;
            case EIGHT -> RANK_EIGHT;
            case NINE -> RANK_NINE;
            case TEN -> RANK_TEN;
            default -> rank.toString().toLowerCase();
        };
        String suitString = suit.toString().toLowerCase();
        return suitString + PATH_SEPARATOR + rankStr + IMAGE_SEPARATOR + suitString + IMAGE_EXTENSION;
    }

    public static String getBackImageName() {
        return BACK_IMAGE_NAME;
    }
}
