package ecr.ac.tec.lenguajes.pokerGUI.config;

import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLTableController;
import ecr.ac.tec.lenguajes.pokerGUI.error.ConsoleErrorHandler;
import ecr.ac.tec.lenguajes.pokerGUI.error.ErrorHandler;
import ecr.ac.tec.lenguajes.pokerGUI.log.ConsoleLog;
import ecr.ac.tec.lenguajes.pokerGUI.log.Log;
import ecr.ac.tec.lenguajes.pokerGUI.controller.FXMLMenuController;
import ecr.ac.tec.lenguajes.pokerGUI.network.SocketNetworkManager;

public final class DefaultConfig implements Config {
    private final Log logger;
    private final ErrorHandler errorHandler;
    private final SocketNetworkManager networkManager;

    public DefaultConfig() {
        this.logger = new ConsoleLog();
        this.errorHandler = new ConsoleErrorHandler(logger);
        this.networkManager = new SocketNetworkManager();
    }

    @Override
    public Object getController(Class<?> type) {
        if (type == FXMLTableController.class) {
            return new FXMLTableController(errorHandler, networkManager);
        }
        if (type == FXMLMenuController.class) {
            return new FXMLMenuController(networkManager);
        }
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
