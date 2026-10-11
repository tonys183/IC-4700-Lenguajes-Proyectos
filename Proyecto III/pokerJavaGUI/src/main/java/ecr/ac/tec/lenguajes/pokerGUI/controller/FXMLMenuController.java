package ecr.ac.tec.lenguajes.pokerGUI.controller;

import ecr.ac.tec.lenguajes.pokerGUI.Main;
import ecr.ac.tec.lenguajes.pokerGUI.context.ClientContext;
import ecr.ac.tec.lenguajes.pokerGUI.network.NetworkManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.io.IOException;

public final class FXMLMenuController implements MenuController {
    private static final String GAME_TABLE_VIEW = "game_table";
    private static final String ALERT_TITLE_MISSING_NAME = "Missing Name";
    private static final String ALERT_CONTENT_MISSING_NAME = "Enter your name to start the game";
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final int EXIT_STATUS = 0;

    private final NetworkManager networkManager;

    public FXMLMenuController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @FXML
    private TextField nameField;
    @FXML
    private TextField ipField;

    @FXML
    @Override
    public void handleStartGame() throws IOException {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(ALERT_TITLE_MISSING_NAME);
            alert.setHeaderText(null);
            alert.setContentText(ALERT_CONTENT_MISSING_NAME);
            alert.showAndWait();
            return;
        }

        // IP Server
        String ip = ipField.getText();
        if (ip == null || ip.trim().isEmpty()) {
            ip = HOST;
        }
        
        networkManager.connect(ip.trim(), PORT);
        ClientContext.getInstance().setPlayerName(name.trim());

        Main.setRoot(GAME_TABLE_VIEW);
    }

    @FXML
    @Override
    public void handleExit() {
        System.exit(EXIT_STATUS);
    }
}
