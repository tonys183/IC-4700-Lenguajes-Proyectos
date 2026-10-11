package ecr.ac.tec.lenguajes.pokerGUI.controller;

import ecr.ac.tec.lenguajes.pokerGUI.context.ClientContext;
import ecr.ac.tec.lenguajes.pokerGUI.model.card.Card;
import ecr.ac.tec.lenguajes.pokerGUI.model.game.GameType;
import ecr.ac.tec.lenguajes.pokerGUI.error.ErrorHandler;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import ecr.ac.tec.lenguajes.pokerGUI.network.NetworkListener;
import ecr.ac.tec.lenguajes.pokerGUI.network.NetworkManager;
import javafx.application.Platform;
import ecr.ac.tec.lenguajes.pokerGUI.view.OpponentView;
import ecr.ac.tec.lenguajes.pokerGUI.view.CardFactory;
import ecr.ac.tec.lenguajes.pokerGUI.command.*;
import ecr.ac.tec.lenguajes.pokerGUI.view.WinnerDisplay;
import javafx.util.Duration;
import ecr.ac.tec.lenguajes.pokerGUI.Main;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;

public final class FXMLTableController implements TableController, NetworkListener {
    private static final String POT_LABEL_PREFIX = "Pot: $";
    private static final String MONEY_LABEL_PREFIX = "Money: $";
    private static final int INITIAL_POT = 0;
    private static final int INITIAL_PLAYER_MONEY = 1000;
    private static final String JOIN_COMMAND_PREFIX = "JOIN:";
    private static final String JOIN_MONEY_SUFFIX = ":1000";
    private static final String PLAYING_PREFIX = "Playing: ";
    private static final String FOLD_COMMAND = "FOLD";
    private static final String CALL_COMMAND = "CALL";
    private static final String RAISE_COMMAND_PREFIX = "RAISE:";
    private static final String CARD_BACK_RANK = "BACK";
    private static final String ROLE_DEALER = "D";
    private static final String ROLE_SMALL_BLIND = "SB";
    private static final String ROLE_BIG_BLIND = "BB";
    private static final String ROLE_DISPLAY_SB = "S";
    private static final String ROLE_DISPLAY_BB = "B";
    private static final String STYLE_ROLE_DEALER = "role-dealer";
    private static final String STYLE_ROLE_SB = "role-sb";
    private static final String STYLE_ROLE_BB = "role-bb";
    private static final String STYLE_ROLE_TOKEN = "role-token";
    private static final String STYLE_ACTIVE_TURN = "active-turn";
    private static final String ACTION_FOLD = "Fold";
    private static final String SEPARATOR = ":";
    private static final String LOG_UNKNOWN_MESSAGE = "[ERROR] Unknown message: ";
    private static final String LOG_ERROR_PROCESSING = "[ERROR] Error processing message: ";
    private static final String ERROR_NAME_TAKEN_CMD = "ERROR:NAME_TAKEN";
    private static final String ALERT_TITLE_ERROR = "Error";
    private static final String ALERT_HEADER_DUPLICATE = "[ERROR] Duplicate Name";
    private static final String ALERT_CONTENT_PREFIX = "The name '";
    private static final String ALERT_CONTENT_SUFFIX = "' is already taken. Choose another name.";
    private static final String VIEW_MAIN_MENU = "main_menu";
    private static final String LOG_ERROR_MENU = "[ERROR] Error returning to menu";
    private static final String DEFAULT_MONEY = "0";
    private static final String CURRENCY_SYMBOL = "$";
    private static final String BUTTON_TEXT_CALL = "Call";
    private static final String BUTTON_TEXT_CHECK = "Check";
    private static final String BUTTON_TEXT_CALL_REGEX = "(?i)Call\\s*\\$?\\s*0(\\.0+)?";
    private static final double OPACITY_FULL = 1.0;
    private static final double OPACITY_ZERO = 0.0;
    private static final double BRIGHTNESS_DIM = -0.6;
    private static final int STATUS_FADE_DURATION_SECONDS = 5;
    private final WinnerDisplay winnerDisplay;

    private final ErrorHandler errorHandler;
    private final NetworkManager networkManager;
    private final Map<String, ClientCommand> commandMap = Map.ofEntries(
            Map.entry("COMMUNITY", new CommunityCommand()),
            Map.entry("GAME", new GameCommand()),
            Map.entry("ROLE", new RoleCommand()),
            Map.entry("CARD", new CardCommand()),
            Map.entry("MONEY", new MoneyCommand()),
            Map.entry("TURN", new TurnCommand()),
            Map.entry("STATUS", new StatusCommand()),
            Map.entry("OK", new OkCommand()),
            Map.entry("PLAYER_JOINED", new PlayerJoinedCommand()),
            Map.entry("PLAYER_LIST", new PlayerListCommand()),
            Map.entry("PLAYER_LEFT", new PlayerLeftCommand()),
            Map.entry("GAME_TYPE", new GameTypeCommand()),
            Map.entry("OPPONENT_CARD", new OpponentCardCommand()),
            Map.entry("POT", new PotCommand()),
            Map.entry("ACTIVE_PLAYER", new ActivePlayerCommand()),
            Map.entry("PLAYER_MONEY", new PlayerMoneyCommand()),
            Map.entry("PLAYER_ROLE", new PlayerRoleCommand()),
            Map.entry("PLAYER_ACTION", new PlayerActionCommand()),
            Map.entry("WINNER", new WinnerCommand()),
            Map.entry("LOBBY_TIMER", new LobbyTimerCommand()));

    private boolean isFolded;
    private final Set<String> currentRoles = new HashSet<>();
    private String currentRole;
    private GameType currentGameType;
    private final CardFactory cardFactory;

    @FXML
    private Label roleLabel;
    private HBox localRoleBox;
    @FXML
    private Label potLabel;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Label playerMoneyLabel;
    @FXML
    private Label playerActionLabel;
    @FXML
    private HBox communityCardsBox;
    @FXML
    private HBox playerHandBox;
    @FXML
    private HBox opponentHandBox;
    @FXML
    private TextField raiseAmountField;
    @FXML
    private Button foldButton;
    @FXML
    private Button callButton;
    @FXML
    private Button raiseButton;
    @FXML
    private Label gameTypeLabel;
    @FXML
    private Label gameStatusLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private Circle connectionStatus;

    public FXMLTableController(ErrorHandler errorHandler, NetworkManager networkManager) {
        this.errorHandler = errorHandler;
        this.networkManager = networkManager;
        this.networkManager.setListener(this);
        this.isFolded = false;
        this.cardFactory = new CardFactory();
        this.winnerDisplay = new WinnerDisplay();
    }

    @FXML
    public void initialize() {
        setGameMode(GameType.HOLDEM);
        updatePot(INITIAL_POT);
        updatePlayerMoney(INITIAL_PLAYER_MONEY);
        playerHandBox.getChildren().clear();
        communityCardsBox.getChildren().clear();
        opponentHandBox.getChildren().clear();
        setControlsEnabled(false);
        
        if (roleLabel != null && roleLabel.getParent() instanceof Pane parent) {
            localRoleBox = new HBox(5);
            localRoleBox.setAlignment(Pos.CENTER);
            localRoleBox.setVisible(false);
            localRoleBox.setManaged(false);
            
            int index = parent.getChildren().indexOf(roleLabel);
            if (index != -1) {
                parent.getChildren().add(index, localRoleBox);
                roleLabel.setVisible(false);
                roleLabel.setManaged(false);
            } else {
                 parent.getChildren().add(localRoleBox);
            }
            updateLocalRoleLabel();
        }

        String playerName = ClientContext.getInstance().getPlayerName();
        if (playerName != null) {
            networkManager.send(JOIN_COMMAND_PREFIX + playerName + JOIN_MONEY_SUFFIX);
        }
    }

    public void setControlsEnabled(boolean enabled) {
        setControlsEnabled(enabled, enabled);
    }

    public void setControlsEnabled(boolean enabled, boolean canFold) {
        foldButton.setDisable(!canFold);
        callButton.setDisable(!enabled);
        raiseButton.setDisable(!enabled);
    }

    public void updateCallButton(double callAmount) {
        if (callAmount == 0) {
            callButton.setText(BUTTON_TEXT_CHECK);
        } else {
            callButton.setText(BUTTON_TEXT_CALL);
        }
    }

    @Override
    public void setGameMode(GameType mode) {
        if (this.currentGameType == mode) {
            return;
        }
        this.currentGameType = mode;
        
        gameTypeLabel.setText(PLAYING_PREFIX + mode.getDisplayName());
        communityCardsBox.setVisible(mode.hasCommunityCards());
        communityCardsBox.setManaged(mode.hasCommunityCards());
        if (!mode.hasCommunityCards()) {
            communityCardsBox.getChildren().clear();
        }
        resetFoldStatus();

        for (Node node : opponentHandBox.getChildren()) {
            if (node instanceof OpponentView opponentView) {
                opponentView.clearCards();
                opponentView.clearRoles();
                opponentView.setFolded(false);
                opponentView.setWinner(false);
                opponentView.setDimmed(false);
            }
        }
        currentRoles.clear();
        updateLocalRoleLabel();
        playerHandBox.setEffect(null);
        playerNameLabel.setStyle("");
    }

    @FXML
    private void handleFold() {
        networkManager.send(FOLD_COMMAND);
        isFolded = true;
        setControlsEnabled(false);
    }

    public boolean isFolded() {
        return isFolded;
    }

    public void resetFoldStatus() {
        isFolded = false;
        playerHandBox.setEffect(null);
    }

    @FXML
    private void handleCall() {
        networkManager.send(CALL_COMMAND);
    }

    @FXML
    private void handleRaise() {
        if (!raiseAmountField.isVisible()) {
            raiseAmountField.setVisible(true);
            raiseAmountField.requestFocus();
        } else {
            String amountText = raiseAmountField.getText();
            if (amountText != null && !amountText.isEmpty()) {
                networkManager.send(RAISE_COMMAND_PREFIX + amountText);
                raiseAmountField.setVisible(false);
                raiseAmountField.clear();
            } else {
                raiseAmountField.setVisible(false);
            }
        }
    }

    @Override
    public void updatePot(int amount) {
        potLabel.setText(POT_LABEL_PREFIX + amount);
    }

    public void updateTimerLabel(String text) {
        Platform.runLater(() -> {
            if (timerLabel != null) {
                timerLabel.setOpacity(1.0);
                timerLabel.setText(text);
                timerLabel.setVisible(true);
            }
        });
    }

    public void hideTimerWithFade() {
        Platform.runLater(() -> {
            if (timerLabel != null && timerLabel.isVisible()) {
                FadeTransition fade = new FadeTransition(Duration.seconds(2), timerLabel);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(event -> timerLabel.setVisible(false));
                fade.play();
            }
        });
    }

    @Override
    public void updatePlayerMoney(int amount) {
        playerMoneyLabel.setText(MONEY_LABEL_PREFIX + amount);
    }

    @Override
    public void updateCommunityCards(Card... cards) {
        if (communityCardsBox.isVisible()) {
            communityCardsBox.getChildren().clear();
            for (Card card : cards) {
                communityCardsBox.getChildren().add(cardFactory.createCardNode(card));
            }
        }
    }

    @Override
    public void updatePlayerHand(Card... cards) {
        playerHandBox.getChildren().clear();
        for (Card card : cards) {
            playerHandBox.getChildren().add(cardFactory.createCardNode(card));
        }
    }

    @Override
    public void updateOpponentHand(int cardCount) {
    }

    public void addOpponent(String name, String money) {
        if (ClientContext.getInstance().getPlayerName().equals(name)) {
            return;
        }
        for (Node node : opponentHandBox.getChildren()) {
            if (node instanceof OpponentView opponentView && opponentView.getPlayerName().equals(name)) {
                return;
            }
        }

        OpponentView opponentView = new OpponentView(name);
        if (money != null) {
            opponentView.setMoney(money);
        }
        opponentHandBox.getChildren().add(opponentView);
    }

    public void addOpponent(String name) {
        addOpponent(name, DEFAULT_MONEY);
    }

    public void removeOpponent(String name) {
        opponentHandBox.getChildren().removeIf(node -> name.equals(node.getUserData()));
    }

    public void addOpponentCard(String opponentName, String rank, String suit) {
        for (Node node : opponentHandBox.getChildren()) {
            if (node instanceof OpponentView opponentView && opponentView.getPlayerName().equals(opponentName)) {
                Node cardNode;
                if (CARD_BACK_RANK.equals(rank)) {
                    cardNode = cardFactory.createSmallCardBackNode();
                } else {
                    cardNode = cardFactory.createSmallCardNode(rank, suit, false);
                }
                opponentView.addCard(cardNode);
                break;
            }
        }
    }

    public void addCardToHand(Card card) {
        addCardToHand(card, false);
    }

    public void addCardToHand(Card card, boolean isVisible) {
        Node cardNode = cardFactory.createCardNode(card, isVisible);
        playerHandBox.getChildren().add(cardNode);
    }

    public void addCommunityCard(Card card) {
        if (communityCardsBox.isVisible()) {
            communityCardsBox.getChildren().add(cardFactory.createCardNode(card));
        }
    }

    private static final Map<String, String> ROLE_STYLE_MAP = Map.of(
            ROLE_DEALER, STYLE_ROLE_DEALER,
            ROLE_SMALL_BLIND, STYLE_ROLE_SB,
            ROLE_BIG_BLIND, STYLE_ROLE_BB);

    public void setPlayerRole(String playerName, String role) {
        String myName = ClientContext.getInstance().getPlayerName();

        if (myName != null && myName.equals(playerName)) {
            if (role == null || role.isEmpty()) {
                return;
            }
            currentRoles.add(role);
            if (ROLE_SMALL_BLIND.equals(role) || ROLE_BIG_BLIND.equals(role)) {
                this.currentRole = role;
            } else if (this.currentRole == null) {
                this.currentRole = role;
            }

            updateLocalRoleLabel();
        } else {
            for (Node node : opponentHandBox.getChildren()) {
                if (node instanceof OpponentView opponentView && opponentView.getPlayerName().equals(playerName)) {
                    opponentView.addRole(role);
                    break;
                }
            }
        }
    }

    private void updateLocalRoleLabel() {
        if (localRoleBox == null) return;
        
        localRoleBox.getChildren().clear();
        
        if (currentRoles.isEmpty()) {
            Label placeholder = new Label(ROLE_DEALER);
            placeholder.getStyleClass().add(STYLE_ROLE_TOKEN);
            placeholder.setOpacity(0);
            localRoleBox.getChildren().add(placeholder);
            
            localRoleBox.setVisible(true);
            localRoleBox.setManaged(true);
            return;
        }

        for (String role : currentRoles) {
            String roleText = role;
            if (ROLE_SMALL_BLIND.equals(role)) {
                roleText = ROLE_DISPLAY_SB;
            } else if (ROLE_BIG_BLIND.equals(role)) {
                roleText = ROLE_DISPLAY_BB;
            }
            
            Label token = new Label(roleText);
            token.getStyleClass().add(STYLE_ROLE_TOKEN);

            String styleClass = ROLE_STYLE_MAP.get(role);
            if (styleClass != null) {
                token.getStyleClass().add(styleClass);
            }
            localRoleBox.getChildren().add(token);
        }

        localRoleBox.setVisible(true);
        localRoleBox.setManaged(true);
        localRoleBox.setOpacity(1);
    }

    public void setPlayerAction(String playerName, String action) {
        if (action != null && action.matches(BUTTON_TEXT_CALL_REGEX)) {
            action = BUTTON_TEXT_CHECK;
        }
        clearAllPlayerActions();
        
        String myName = ClientContext.getInstance().getPlayerName();
        if (myName != null && myName.equals(playerName)) {
            playerActionLabel.setText(action);
            playerActionLabel.setOpacity(OPACITY_FULL);
            if (ACTION_FOLD.equalsIgnoreCase(action)) {
                ColorAdjust darken = new ColorAdjust();
                darken.setBrightness(BRIGHTNESS_DIM);
                playerHandBox.setEffect(darken);
            }
        } else {
            for (Node node : opponentHandBox.getChildren()) {
                if (node instanceof OpponentView opponentView && opponentView.getPlayerName().equals(playerName)) {
                    opponentView.setAction(action);
                    if (ACTION_FOLD.equalsIgnoreCase(action)) {
                        opponentView.setFolded(true);
                    }
                    break;
                }
            }
        }
    }

    private void clearAllPlayerActions() {
        playerActionLabel.setText("");
        for (Node node : opponentHandBox.getChildren()) {
            if (node instanceof OpponentView opponentView) {
                opponentView.clearAction();
            }
        }
    }

    @Override
    public void showGameStatus(String message) {
        showStatusMessage(message, STATUS_FADE_DURATION_SECONDS);
    }

    public void showStatusMessage(String message, int durationSeconds) {
        Platform.runLater(() -> {
            gameStatusLabel.setText(message);
            gameStatusLabel.setVisible(true);
            gameStatusLabel.setManaged(true);
            gameStatusLabel.setOpacity(OPACITY_FULL);
            FadeTransition fade = new FadeTransition(Duration.seconds(durationSeconds), gameStatusLabel);
            fade.setFromValue(OPACITY_FULL);
            fade.setToValue(OPACITY_ZERO);
            fade.setOnFinished(e -> {
                gameStatusLabel.setVisible(false);
                gameStatusLabel.setManaged(false);
            });
            fade.play();
        });
    }

    @Override
    public void onMessage(String message) {
        Platform.runLater(() -> {
            try {
                processMessage(message);
            } catch (Exception e) {
                errorHandler.handle(LOG_ERROR_PROCESSING + message, e);
            }
        });
    }

    @Override
    public void onConnectionStatusChanged(boolean isConnected) {
        Platform.runLater(() -> {
            if (connectionStatus != null) {
                connectionStatus.setFill(isConnected ? Color.GREEN : Color.RED);
            }
        });
    }

    public void setActivePlayer(String activePlayerName) {
        String myName = ClientContext.getInstance().getPlayerName();
        if (myName != null && myName.equals(activePlayerName)) {
            playerNameLabel.getStyleClass().add(STYLE_ACTIVE_TURN);
        } else {
            playerNameLabel.getStyleClass().remove(STYLE_ACTIVE_TURN);
        }

        for (Node node : opponentHandBox.getChildren()) {
            if (node instanceof OpponentView opponentView) {
                opponentView.setActive(activePlayerName.equals(opponentView.getPlayerName()));
            }
        }
    }

    public void updatePlayerMoney(String playerName, String amount) {
        String myName = ClientContext.getInstance().getPlayerName();

        if (myName != null && myName.equals(playerName)) {
            playerMoneyLabel.setText(CURRENCY_SYMBOL + amount);
        } else {
            for (Node node : opponentHandBox.getChildren()) {
                if (node instanceof OpponentView opponentView && opponentView.getPlayerName().equals(playerName)) {
                    opponentView.setMoney(amount);
                    break;
                }
            }
        }
    }

    private void processMessage(String message) {
        if (ERROR_NAME_TAKEN_CMD.equals(message)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(ALERT_TITLE_ERROR);
                alert.setHeaderText(ALERT_HEADER_DUPLICATE);
                alert.setContentText(ALERT_CONTENT_PREFIX + ClientContext.getInstance().getPlayerName() + ALERT_CONTENT_SUFFIX);
                alert.showAndWait();
                try {
                    Main.setRoot(VIEW_MAIN_MENU);
                } catch (IOException e) {
                    errorHandler.handle(LOG_ERROR_MENU, e);
                }
            });
            return;
        }

        String[] parts = message.split(SEPARATOR);
        String commandName = parts[0];

        ClientCommand command = commandMap.get(commandName);
        if (command != null) {
            command.execute(this, parts);
        } else {
            System.out.println(LOG_UNKNOWN_MESSAGE + message);
        }
    }

    public String getPlayerRole() {
        return currentRole;
    }

    public int getCommunityCardCount() {
        return communityCardsBox.getChildren().size();
    }

    public void showWinner(String winnerName, Map<String, List<String>> playerHands) {
        winnerDisplay.showWinner(this, winnerName, playerHands);
    }

    public Label getPlayerNameLabel() {
        return playerNameLabel;
    }

    public HBox getPlayerHandBox() {
        return playerHandBox;
    }

    public HBox getOpponentHandBox() {
        return opponentHandBox;
    }

    public CardFactory getCardFactory() {
        return cardFactory;
    }
}
