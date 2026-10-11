package ecr.ac.tec.lenguajes.pokerServer.server;

import ecr.ac.tec.lenguajes.pokerServer.game.managers.PlayerManager;
import ecr.ac.tec.lenguajes.pokerServer.server.c.CComunicator;
import ecr.ac.tec.lenguajes.pokerServer.server.c.CConnector;

public final class ServerContext {
    private static final String EVALUATOR_NOT_CONNECTED_MSG = "[ERROR] C evaluator not connected";

    private PlayerManager controller;
    private CConnector cEvaluator;
    private final CComunicator cComunicator;

    public ServerContext(PlayerManager initialController,
            CComunicator cComunicator) {
        this.controller = initialController;
        this.cComunicator = cComunicator;
    }

    public CComunicator getCComunicator() {
        return cComunicator;
    }

    public synchronized void setController(PlayerManager controller) {
        this.controller = controller;
    }

    public synchronized PlayerManager getController() {
        return controller;
    }

    public synchronized void setCEvaluator(CConnector cEvaluator) {
        this.cEvaluator = cEvaluator;
    }

    public synchronized CConnector getCEvaluator() {
        if (cEvaluator == null) {
            throw new IllegalStateException(EVALUATOR_NOT_CONNECTED_MSG);
        }
        return cEvaluator;
    }
}
