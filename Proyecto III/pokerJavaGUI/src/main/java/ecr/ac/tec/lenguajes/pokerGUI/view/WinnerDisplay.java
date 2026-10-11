package ecr.ac.tec.lenguajes.pokerGUI.view;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;
import ecr.ac.tec.lenguajes.pokerGUI.context.ClientContext;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;

public class WinnerDisplay {
    private static final String WINNER_STYLE = "-fx-border-color: gold; -fx-border-width: 2px; -fx-border-radius: 3px; -fx-padding: 2px; -fx-text-fill: gold;";
    private static final String DEFAULT_STYLE = "";
    private static final String WINNER_MESSAGE = "WINNER: ";
    private static final double BRIGHTNESS_DIM = -0.6;
    private static final int WINNER_DISPLAY_DURATION_SECONDS = 10;

    public void showWinner(FXMLTableController controller, String winnerName, Map<String, List<String>> playerHands) {
        Platform.runLater(() -> {
            controller.showStatusMessage(WINNER_MESSAGE + winnerName, WINNER_DISPLAY_DURATION_SECONDS);

            String myName = ClientContext.getInstance().getPlayerName();
            Label playerNameLabel = controller.getPlayerNameLabel();
            HBox playerHandBox = controller.getPlayerHandBox();
            HBox opponentHandBox = controller.getOpponentHandBox();
            CardFactory cardFactory = controller.getCardFactory();

            if (myName != null) {
                if (myName.equals(winnerName)) {
                    playerNameLabel.setStyle(WINNER_STYLE);
                    playerHandBox.setEffect(null);
                } else {
                    playerNameLabel.setStyle(DEFAULT_STYLE);
                    ColorAdjust darken = new ColorAdjust();
                    darken.setBrightness(BRIGHTNESS_DIM);
                    playerHandBox.setEffect(darken);
                }
                if (playerHands.containsKey(myName)) {
                }
            }

            for (Node node : opponentHandBox.getChildren()) {
                if (node instanceof OpponentView opponentView) {
                    String oppName = opponentView.getPlayerName();
                    opponentView.setFolded(false);
                    if (oppName.equals(winnerName)) {
                        opponentView.setWinner(true);
                        opponentView.setDimmed(false);
                    } else {
                        opponentView.setWinner(false);
                        opponentView.setDimmed(true);
                    }
                    if (playerHands.containsKey(oppName)) {
                        opponentView.clearCards();
                        List<String> cards = playerHands.get(oppName);
                        for (String cardStr : cards) {
                            String[] parts = cardStr.split("-");
                            if (parts.length == 2) {
                                Node cardNode = cardFactory.createSmallCardNode(parts[0], parts[1], true);
                                opponentView.addCard(cardNode);
                            }
                        }
                    }
                }
            }
        });
    }
}
