package ecr.ac.tec.lenguajes.pokerServer.server;

import ecr.ac.tec.lenguajes.pokerServer.server.command.game.GameCommand;

public final class GameLoop implements Runnable {
    private static final String START_MSG = "Game Loop started";
    private static final String ERROR_MSG_PREFIX = "[ERROR] Error executing command: ";

    private final ServerContext context;
    private final ServerMessageQueue queue;
    private volatile boolean running = true;

    public GameLoop(ServerContext context, ServerMessageQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println(START_MSG);
        while (running) {
            GameCommand command = queue.dequeue();
            if (command != null) {
                try {
                    command.execute(context);
                } catch (Exception e) {
                    System.err.println(ERROR_MSG_PREFIX + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
