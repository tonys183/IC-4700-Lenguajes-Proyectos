package ecr.ac.tec.lenguajes.pokerGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import ecr.ac.tec.lenguajes.pokerGUI.config.Config;
import ecr.ac.tec.lenguajes.pokerGUI.config.DefaultConfig;

public final class Main extends Application {
    private static final String MAIN_MENU_VIEW = "main_menu";
    private static final String STYLES_PATH = "/styles.css";
    private static final String APP_TITLE = "Poker Horse";
    private static final String FXML_PATH_PREFIX = "/fxml/";
    private static final String FXML_EXTENSION = ".fxml";

    private static final Config config = new DefaultConfig();
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML(MAIN_MENU_VIEW));
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLES_PATH)).toExternalForm());
        stage.setTitle(APP_TITLE);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(FXML_PATH_PREFIX + fxml + FXML_EXTENSION));
        fxmlLoader.setControllerFactory(config::getController);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
