package ecr.ac.tec.lenguajes.pokerGUI.view;

import ecr.ac.tec.lenguajes.pokerGUI.model.card.Card;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import java.util.Objects;

public class CardFactory {
    private static final String CARD_IMAGE_PATH_PREFIX = "/images/cards/";
    private static final String CARD_LABEL_FALLBACK_STYLE = "card-label-fallback";
    private static final String CARD_BACK_IMAGE = "card_back.png";
    private static final String CARD_IMAGE_ERROR = "[ERROR] loading card image: ";
    private static final String CARD_BACK_IMAGE_ERROR = "[ERROR] loading card back image: ";
    private static final String CARD_BACK_FALLBACK_TEXT = "[BACK]";
    private static final String EYE_ICON_SVG = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";
    private static final String EYE_ICON_STYLE = "-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 3; -fx-background-radius: 5;";
    private static final String CARD_IMAGE_SEPARATOR = "_of_";
    private static final String CARD_IMAGE_EXTENSION = ".png";
    private static final String RANK_TWO = "TWO";
    private static final String RANK_THREE = "THREE";
    private static final String RANK_FOUR = "FOUR";
    private static final String RANK_FIVE = "FIVE";
    private static final String RANK_SIX = "SIX";
    private static final String RANK_SEVEN = "SEVEN";
    private static final String RANK_EIGHT = "EIGHT";
    private static final String RANK_NINE = "NINE";
    private static final String RANK_TEN = "TEN";
    private static final String TWO_MAPPED = "2";
    private static final String THREE_MAPPED = "3";
    private static final String FOUR_MAPPED = "4";
    private static final String FIVE_MAPPED = "5";
    private static final String SIX_MAPPED = "6";
    private static final String SEVEN_MAPPED = "7";
    private static final String EIGHT_MAPPED = "8";
    private static final String NINE_MAPPED = "9";
    private static final String TEN_MAPPED = "10";
    private static final String PATH_SEPARATOR = "/";
    private static final int DEFAULT_CARD_WIDTH = 85;
    private static final int SMALL_CARD_WIDTH = 70;

    public Node createCardNode(Card card) {
        return createCardNode(card, DEFAULT_CARD_WIDTH, false);
    }

    public Node createCardNode(Card card, boolean isVisible) {
        return createCardNode(card, DEFAULT_CARD_WIDTH, isVisible);
    }

    public Node createCardNode(Card card, int width) {
        return createCardNode(card, width, false);
    }

    public Node createCardNode(Card card, int width, boolean isVisible) {
        Node cardNode;
        String imagePath = CARD_IMAGE_PATH_PREFIX + card.getImageName();
        try {
            cardNode = createImageView(imagePath, width);
        } catch (Exception e) {
            System.err.println(CARD_IMAGE_ERROR + imagePath);
            cardNode = createFallbackLabel(card.toString());
        }

        if (isVisible) {
            return addVisibilityOverlay(cardNode);
        }

        return cardNode;
    }

    public Node createCardBackNode() {
        return createCardBackNode(DEFAULT_CARD_WIDTH);
    }

    public Node createSmallCardBackNode() {
        return createCardBackNode(SMALL_CARD_WIDTH);
    }

    private Node createCardBackNode(int width) {
        try {
            String imagePath = CARD_IMAGE_PATH_PREFIX + CARD_BACK_IMAGE;
            return createImageView(imagePath, width);
        } catch (Exception e) {
            System.err.println(CARD_BACK_IMAGE_ERROR + e.getMessage());
            e.printStackTrace();
            return createFallbackLabel(CARD_BACK_FALLBACK_TEXT);
        }
    }

    public Node createSmallCardNode(String rank, String suit) {
        return createSmallCardNode(rank, suit, false);
    }

    public Node createSmallCardNode(String rank, String suit, boolean isVisible) {
        String rankStr = convertRank(rank);
        String suitStr = suit.toLowerCase();
        String imageName = suitStr + PATH_SEPARATOR + rankStr + CARD_IMAGE_SEPARATOR + suitStr + CARD_IMAGE_EXTENSION;
        String imagePath = CARD_IMAGE_PATH_PREFIX + imageName;
        Node cardNode;
        try {
            cardNode = createImageView(imagePath, SMALL_CARD_WIDTH);
        } catch (Exception e) {
            cardNode = createFallbackLabel(rank + suit);
        }

        if (isVisible) {
            return addVisibilityOverlay(cardNode);
        }
        return cardNode;
    }

    private String convertRank(String rank) {
        String rankStr = rank.toUpperCase();
        return switch (rankStr) {
            case RANK_TWO -> TWO_MAPPED;
            case RANK_THREE -> THREE_MAPPED;
            case RANK_FOUR -> FOUR_MAPPED;
            case RANK_FIVE -> FIVE_MAPPED;
            case RANK_SIX -> SIX_MAPPED;
            case RANK_SEVEN -> SEVEN_MAPPED;
            case RANK_EIGHT -> EIGHT_MAPPED;
            case RANK_NINE -> NINE_MAPPED;
            case RANK_TEN -> TEN_MAPPED;
            default -> rankStr.toLowerCase();
        };
    }

    private Node addVisibilityOverlay(Node cardNode) {
        StackPane wrapper = new StackPane(cardNode);

        SVGPath eyePath = new SVGPath();
        eyePath.setContent(EYE_ICON_SVG);
        eyePath.setFill(Color.WHITE);
        eyePath.setScaleX(0.7);
        eyePath.setScaleY(0.7);

        Label eyeIcon = new Label();
        eyeIcon.setGraphic(eyePath);
        eyeIcon.setStyle(EYE_ICON_STYLE);
        eyeIcon.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        wrapper.getChildren().add(eyeIcon);
        StackPane.setAlignment(eyeIcon, Pos.TOP_RIGHT);
        return wrapper;
    }

    private Node createImageView(String imagePath, int width) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private Node createFallbackLabel(String text) {
        Label fallbackLabel = new Label(text);
        fallbackLabel.getStyleClass().add(CARD_LABEL_FALLBACK_STYLE);
        return fallbackLabel;
    }
}
