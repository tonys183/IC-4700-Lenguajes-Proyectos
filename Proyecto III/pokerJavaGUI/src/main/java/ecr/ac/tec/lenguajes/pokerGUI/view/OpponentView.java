package ecr.ac.tec.lenguajes.pokerGUI.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class OpponentView extends VBox {
    private static final String OPPONENT_LABEL = "opponent-label";
    private static final String STATUS_LABEL = "status-label";
    private static final String ROLE_TOKEN = "role-token";
    private static final String ACTION_LABEL = "action-label";
    private static final String ACTIVE_TURN = "active-turn";
    private static final String MONEY_FONT_SIZE = "-fx-font-size: 12px;";
    private static final String ROLE_DEALER = "D";
    private static final String ROLE_SB = "SB";
    private static final String ROLE_BB = "BB";
    private static final String ROLE_SB_DISPLAY = "S";
    private static final String ROLE_BB_DISPLAY = "B";
    private static final String ROLE_DEALER_MAPPED = "role-dealer";
    private static final String ROLE_SB_MAPPED = "role-sb";
    private static final String ROLE_BB_MAPPED = "role-bb";
    private static final String MONEY_PREFIX = "$";
    private static final String WINNER_STYLE = "-fx-border-color: gold; -fx-border-width: 2px; -fx-border-radius: 3px; -fx-padding: 2px; -fx-text-fill: gold;";
    private static final String LOSE_STYLE = "";
    private static final double DARK_BRIGHTNESS = -0.6;

    private final Label nameLabel;
    private final Label moneyLabel;
    private final HBox roleBox;
    private final Label actionLabel;
    private final HBox cardsBox;
    private final String playerName;
    private final Set<String> roles = new HashSet<>();

    public OpponentView(String playerName) {
        this.playerName = playerName;
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5);
        this.setUserData(playerName);

        nameLabel = new Label(playerName);
        nameLabel.getStyleClass().add(OPPONENT_LABEL);

        moneyLabel = new Label();
        moneyLabel.getStyleClass().add(STATUS_LABEL);
        moneyLabel.setStyle(MONEY_FONT_SIZE);

        roleBox = new HBox(2);
        roleBox.setAlignment(Pos.CENTER);
        roleBox.setVisible(false);
        roleBox.setManaged(false);

        actionLabel = new Label();
        actionLabel.getStyleClass().add(ACTION_LABEL);

        cardsBox = new HBox(-40);
        cardsBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(nameLabel, moneyLabel, roleBox, cardsBox, actionLabel);
        updateRoleLabel();
    }

    public void addCard(Node cardNode) {
        cardsBox.getChildren().add(cardNode);
        cardsBox.requestLayout();
    }

    public void clearCards() {
        cardsBox.getChildren().clear();
    }

    private static final Map<String, String> ROLE_STYLE_MAP = Map.of(
            ROLE_DEALER, ROLE_DEALER_MAPPED,
            ROLE_SB, ROLE_SB_MAPPED,
            ROLE_BB, ROLE_BB_MAPPED);

    public void setRole(String role) {
        addRole(role);
    }

    public void addRole(String role) {
        if (role == null || role.isEmpty()) {
            return;
        }
        roles.add(role);
        updateRoleLabel();
    }

    public void clearRoles() {
        roles.clear();
        updateRoleLabel();
    }

    private void updateRoleLabel() {
        roleBox.getChildren().clear();
        if (roles.isEmpty()) {
            Label placeholder = new Label(ROLE_DEALER);
            placeholder.getStyleClass().add(ROLE_TOKEN);
            placeholder.setOpacity(0);
            roleBox.getChildren().add(placeholder);
            
            roleBox.setVisible(true);
            roleBox.setManaged(true);
            return;
        }

        for (String role : roles) {
            Label token = new Label(getRoleDisplayText(role));
            token.getStyleClass().add(ROLE_TOKEN);
            
            String styleClass = ROLE_STYLE_MAP.get(role);
            if (styleClass != null) {
                token.getStyleClass().add(styleClass);
            }
            roleBox.getChildren().add(token);
        }

        roleBox.setVisible(true);
        roleBox.setManaged(true);
        roleBox.setOpacity(1);
    }


    public String getPlayerName() {
        return playerName;
    }

    public void setMoney(String amount) {
        moneyLabel.setText(MONEY_PREFIX + amount);
    }

    public void setAction(String action) {
        actionLabel.setText(action);
        actionLabel.setOpacity(1.0);
    }

    public void clearAction() {
        actionLabel.setText("");
    }

    public void setActive(boolean active) {
        if (active) {
            if (!nameLabel.getStyleClass().contains(ACTIVE_TURN)) {
                nameLabel.getStyleClass().add(ACTIVE_TURN);
            }
        } else {
            nameLabel.getStyleClass().remove(ACTIVE_TURN);
        }
    }

    private String getRoleDisplayText(String role) {
        if (ROLE_SB.equals(role)) {
            return ROLE_SB_DISPLAY;
        } else if (ROLE_BB.equals(role)) {
            return ROLE_BB_DISPLAY;
        }
        return role;
    }
    public void setFolded(boolean folded) {
        if (folded) {
            javafx.scene.effect.ColorAdjust darken = new javafx.scene.effect.ColorAdjust();
            darken.setBrightness(DARK_BRIGHTNESS);
            cardsBox.setEffect(darken);
        } else {
            cardsBox.setEffect(null);
        }
    }
    public void setWinner(boolean isWinner) {
        if (isWinner) {
            nameLabel.setStyle(WINNER_STYLE);
        } else {
            nameLabel.setStyle(LOSE_STYLE);
        }
    }

    public void setDimmed(boolean dimmed) {
        if (dimmed) {
            javafx.scene.effect.ColorAdjust darken = new javafx.scene.effect.ColorAdjust();
            darken.setBrightness(DARK_BRIGHTNESS);
            this.setEffect(darken);
        } else {
            this.setEffect(null);
        }
    }
}
