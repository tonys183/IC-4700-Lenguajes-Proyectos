package ecr.ac.tec.lenguajes.pokerGUI.model.game;

public enum GameType {
    HOLDEM("Texas Hold'em", true),
    OMAHA("Omaha High", true),
    RAZZ("Razz", false),
    SEVEN_CARD_STUD("Seven Card Stud", false),
    EIGHT_OR_BETTER("Omaha Hi/Lo", true);

    private final String displayName;
    private final boolean hasCommunityCards;

    GameType(String displayName, boolean hasCommunityCards) {
        this.displayName = displayName;
        this.hasCommunityCards = hasCommunityCards;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean hasCommunityCards() {
        return hasCommunityCards;
    }
}
