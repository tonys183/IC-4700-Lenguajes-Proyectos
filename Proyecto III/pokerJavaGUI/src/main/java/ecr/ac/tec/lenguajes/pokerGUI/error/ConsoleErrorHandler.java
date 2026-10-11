package ecr.ac.tec.lenguajes.pokerGUI.error;

import ecr.ac.tec.lenguajes.pokerGUI.log.Log;

public final class ConsoleErrorHandler implements ErrorHandler {
    private final Log logger;

    public ConsoleErrorHandler(Log logger) {
        this.logger = logger;
    }

    @Override
    public void handle(String message, Exception e) {
        logger.error(message, e);
    }
}
